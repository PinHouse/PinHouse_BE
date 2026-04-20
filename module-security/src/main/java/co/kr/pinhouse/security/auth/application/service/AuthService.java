package co.kr.pinhouse.security.auth.application.service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.SecurityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.util.KeyUtil;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.onboarding.TempUserInfo;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import co.kr.pinhouse.security.auth.application.dto.response.AuthExchangeResponse;
import co.kr.pinhouse.security.auth.application.usecase.AuthUseCase;
import co.kr.pinhouse.security.jwt.application.dto.request.JwtTokenRequest;
import co.kr.pinhouse.security.jwt.application.dto.response.JwtTokenResponse;
import co.kr.pinhouse.security.jwt.application.util.JwtProvider;
import co.kr.pinhouse.security.jwt.application.util.JwtValidator;
import co.kr.pinhouse.security.jwt.domain.entity.JwtExchangeCode;
import co.kr.pinhouse.security.jwt.domain.entity.JwtExchangeCodeType;
import co.kr.pinhouse.security.jwt.domain.entity.JwtRefreshToken;
import co.kr.pinhouse.security.jwt.domain.repository.JwtExchangeCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

	/// 유저 저장소
	private final UserJpaRepository repository;

	/// 토큰 의존성
	private final JwtValidator jwtValidator;
	private final JwtProvider jwtProvider;

	/// Exchange code 저장소
	private final JwtExchangeCodeRepository exchangeCodeRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final KeyUtil keyUtil;

	@Value("${auth.exchange.code.ttl-seconds:60}")
	private long exchangeCodeTtlSeconds;

	@Value("${auth.oauth2.temp-user.ttl-minutes:5}")
	private long tempUserTtlMinutes;

	// =================
	//  퍼블릭 로직
	// =================

	/// 액세스/리프레쉬 토큰 발급
	@Override
	@Transactional
	public JwtTokenResponse issueTokens(User user) {

		/// 인증된 유저에게 JWT 발급하기
		var jwtRequest = JwtTokenRequest.from(user);

		/// 액세스토큰/리프레쉬 토큰 발급
		String accessToken = jwtProvider.createAccessToken(jwtRequest);
		String refreshToken = jwtProvider.createRefreshToken(jwtRequest);

		return JwtTokenResponse.of(accessToken, refreshToken);
	}

	/// 로그아웃
	@Override
	@Transactional
	public void logout(UUID userId, Optional<String> refreshToken) {

		/// DB 검증
		User user = repository.findById(userId)
			.orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_ID));

		/// 없다면 예외처리
		if (refreshToken.isEmpty()) {
			throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		/// 레디스에서 삭제하도록 로직 수행
		jwtValidator.removeRefreshToken(user.getId(), refreshToken.get());
	}

	/// 토큰 여부 및 검증성 체크
	@Override
	public boolean checkToken(Optional<String> optionalAccessToken) {

		/// 토큰이 없으면 false 반환
		if (optionalAccessToken.isEmpty()) {
			return false;
		}

		// 토큰이 있다면 검증 시도
		String jwtToken = optionalAccessToken.get();
		jwtValidator.validateAccessToken(jwtToken);

		/// 검증 성공 시 true 반환
		return true;
	}

	@Override
	@Transactional
	public String createExchangeCode(User user) {
		String exchangeCode = keyUtil.generateExchangeCode();
		JwtExchangeCode codeEntity = JwtExchangeCode.tokenIssuable(user.getId(), exchangeCode, exchangeCodeTtlSeconds);
		exchangeCodeRepository.save(codeEntity);

		return exchangeCode;
	}

	@Override
	@Transactional
	public String createSignupExchangeCode(TempUserInfo tempUserInfo) {
		String tempUserKey = keyUtil.generateOAuth2TempUserKey();
		redisTemplate.opsForValue().set(tempUserKey, tempUserInfo, Duration.ofMinutes(tempUserTtlMinutes));

		String exchangeCode = keyUtil.generateExchangeCode();
		JwtExchangeCode codeEntity = JwtExchangeCode.signupRequired(tempUserKey, exchangeCode, exchangeCodeTtlSeconds);
		exchangeCodeRepository.save(codeEntity);

		return exchangeCode;
	}

	/// 토큰 재발급
	@Override
	@Transactional
	public JwtTokenResponse reissue(Optional<String> refreshToken) {

		/// 없다면 예외처리
		if (refreshToken.isEmpty()) {
			throw new CustomException(SecurityErrorCode.REFRESH_TOKEN_NOT_FOUND);
		}

		/// 존재하는 리프레쉬 토큰 검증
		JwtRefreshToken token = jwtValidator.validateRefreshToken(refreshToken.get());

		/// 리프레쉬 토큰 바탕으로 조회
		User user = repository.findById(token.getUserId())
			.orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_ID));

		/// 기존 리프레쉬 토큰 무효화하기 (RDB)
		jwtValidator.removeRefreshToken(user.getId(), token.getRefreshToken());

		return issueTokens(user);
	}

	/// Exchange code 처리
	@Override
	@Transactional
	public AuthExchangeResponse exchangeCode(String exchangeCode) {

		/// Exchange code 검증 및 조회
		JwtExchangeCode codeEntity = exchangeCodeRepository.findByExchangeCode(exchangeCode)
			.orElseThrow(() -> new CustomException(SecurityErrorCode.EXCHANGE_CODE_INVALID));

		/// Exchange code 즉시 삭제 (one-time 보장)
		exchangeCodeRepository.delete(codeEntity);

		if (codeEntity.getExchangeCodeType() == JwtExchangeCodeType.SIGNUP_REQUIRED) {
			log.info("Exchange code 사용 완료 - signup required, tempKey: {}", codeEntity.getTempUserKey());
			return AuthExchangeResponse.signupRequired(codeEntity.getTempUserKey());
		}

		if (codeEntity.getExchangeCodeType() != JwtExchangeCodeType.TOKEN_ISSUABLE) {
			throw new CustomException(SecurityErrorCode.EXCHANGE_CODE_INVALID);
		}

		/// 사용자 조회
		User user = repository.findById(codeEntity.getUserId())
			.orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_ID));

		log.info("Exchange code 사용 완료 - userId: {}", user.getId());

		/// 토큰 발급
		JwtTokenResponse tokenResponse = issueTokens(user);
		return AuthExchangeResponse.tokenIssued(tokenResponse.accessToken(), tokenResponse.refreshToken());
	}

}
