package com.pinHouse.server.security.jwt.util;

import java.util.UUID;

public class TokenNameUtil {

    // 쿠키 이름
    public static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "REFRESH_TOKEN";

    ///
    public static final String ACCESS_TOKEN_SUBJECT = "Authorization";
    public static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";

    /// JWT
    public static final String ID_CLAIM = "user_id";
    public static final String EMAIL_CLAIM = "email";
    public static final String ROLE_CLAIM = "role";
    public static final String ROLE_PREFIX = "ROLE_";

    /// 레디스 키
    public static String getTokenKey(UUID userId) {
        return "refreshToken:" + userId;
    }
}
