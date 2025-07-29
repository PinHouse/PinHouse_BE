package com.pinHouse.server.platform.domain.user;

import com.pinHouse.server.security.oauth2.domain.OAuth2UserInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class User {

    private UUID id;
    private Provider provider;
    private String socialId;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
    private String profileImage;

    /// 정적 팩토리 메서드
    public static User of(OAuth2UserInfo userInfo) {
        return User.builder()
                .id(UUID.randomUUID())
                .provider(Provider.valueOf(userInfo.getProvider()))
                .socialId(userInfo.getProviderId())
                .name(userInfo.getUserName())
                .email(userInfo.getEmail())
                .phoneNumber("phoneNumber")
                .profileImage(userInfo.getImageUrl())
                .role(Role.USER)
                .build();
    }
}
