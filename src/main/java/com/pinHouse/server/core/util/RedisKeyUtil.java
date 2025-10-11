package com.pinHouse.server.core.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisKeyUtil {

    /// 공통 키
    private static final String SEPARATOR = ":";
    private static final String DELIMITER = "-";

    /// 합쳐서 사용하는 키 목록

    /// OAUTH
    private static final String OAUTH2_TEMP_USER = "OAUTH2_TEMP_USER:";


    /// JWT
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String ID_CLAIM = "user_id";
    public static final String ROLE_CLAIM = "role";

    // =====================
    //  합쳐서 사용하는 키 목록
    // =====================

    /// 키 생성 함수
    public String generateOAuth2TempUserKey() {
        return OAUTH2_TEMP_USER + UUID.randomUUID();
    }

    public static String getRefreshTokenKey(UUID userId) {
        return REFRESH_TOKEN + SEPARATOR + userId ;
    }

}
