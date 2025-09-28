package com.pinHouse.server.security.oauth2.handler;

import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class SignupRequiredException extends AuthenticationException {

    private final TempUserInfo userInfo;

    public SignupRequiredException(TempUserInfo userInfo) {
        super("SIGNUP_REQUIRED");
        this.userInfo = userInfo;
    }

}
