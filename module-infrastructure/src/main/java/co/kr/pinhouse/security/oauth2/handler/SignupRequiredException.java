package co.kr.pinhouse.security.oauth2.handler;

import org.springframework.security.core.AuthenticationException;

import co.kr.pinhouse.domain.user.domain.onboarding.TempUserInfo;
import lombok.Getter;

@Getter
public class SignupRequiredException extends AuthenticationException {

	private final TempUserInfo userInfo;

	public SignupRequiredException(TempUserInfo userInfo) {
		super("SIGNUP_REQUIRED");
		this.userInfo = userInfo;
	}

}
