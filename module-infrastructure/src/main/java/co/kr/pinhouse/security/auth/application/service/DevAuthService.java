package co.kr.pinhouse.security.auth.application.service;

import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.SecurityErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import co.kr.pinhouse.security.jwt.application.dto.JwtTokenRequest;
import co.kr.pinhouse.security.jwt.application.dto.JwtTokenResponse;
import co.kr.pinhouse.security.jwt.application.util.JwtProvider;
import co.kr.pinhouse.security.oauth2.domain.PrincipalDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DevAuthService {

	/// 유저 저장
	private final UserJpaRepository repository;

	/// 토큰 발급
	private final JwtProvider tokenProvider;

	/// 개발자용 UUID
	private final UUID id = UUID.fromString("12345678-aaaa-bbbb-cccc-123456789abc");

	// =================
	//  퍼블릭 로직
	// =================

	/// 개발용 토큰 생성
	@Transactional
	public JwtTokenResponse devCreate() {

		/// 테스트용 유저 정보 수정
		User user;

		/// 유저 생성하기
		if (repository.existsById(id)) {
			user = repository.findById(id)
					.orElseThrow(() -> new CustomException(SecurityErrorCode.NOT_FOUND_ID));
		} else {
			User dev = User.devOf(id);
			user = repository.save(dev);
		}

		/// 인증필터에 적용
		PrincipalDetails principalDetails = PrincipalDetails.of(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		/// 토큰 발급하기
		var tokenRequest = JwtTokenRequest.from(user);
		String devAccessToken = tokenProvider.createDevAccessToken(tokenRequest);
		String refreshToken = tokenProvider.createRefreshToken(tokenRequest);

		/// 리턴
		return JwtTokenResponse.of(devAccessToken, refreshToken);
	}

}
