package com.pinHouse.server.security.oauth2.domain;

import com.pinHouse.server.platform.user.domain.entity.Gender;
import lombok.*;

/**
 * 회원가입을 위한 리다이렉트
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempUserInfo {
    private String socialId;
    private String social;
    private String email;
    private String username;
    private String gender;
    private String birthday;

    /// 온보딩을 위한 정보를 포함하는 도메인
    public static TempUserInfo from (OAuth2UserInfo userInfo) {
        return TempUserInfo.builder()
                .social(userInfo.getProvider())
                .socialId(userInfo.getProviderId())
                .email(userInfo.getEmail())
                .username(userInfo.getUserName())
                .gender(userInfo.getGender() == null ? Gender.Other.name() : userInfo.getGender())
                .birthday(userInfo.getBirthYear() + userInfo.getBirthday())
                .build();
    }

}
