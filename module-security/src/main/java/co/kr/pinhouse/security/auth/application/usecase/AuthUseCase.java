package co.kr.pinhouse.security.auth.application.usecase;

import java.util.Optional;
import java.util.UUID;

import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.onboarding.TempUserInfo;
import co.kr.pinhouse.security.auth.application.dto.response.AuthExchangeResponse;
import co.kr.pinhouse.security.jwt.application.dto.response.JwtTokenResponse;

public interface AuthUseCase {

	/// 액세스/리프레쉬 토큰 발급
	JwtTokenResponse issueTokens(User user);

	/// 재발급 하기
	JwtTokenResponse reissue(Optional<String> refreshToken);

	/// 로그아웃 진행하기
	void logout(UUID userId, Optional<String> refreshToken);

	/// 토큰 여부 판단하기
	boolean checkToken(Optional<String> accessToken);

	/// 기존 유저용 Exchange code 생성
	String createExchangeCode(User user);

	/// 회원가입 필요 유저용 Exchange code 생성
	String createSignupExchangeCode(TempUserInfo tempUserInfo);

	/// Exchange code 처리
	AuthExchangeResponse exchangeCode(String exchangeCode);

}
