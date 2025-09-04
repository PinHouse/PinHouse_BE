package com.pinHouse.server.platform.user.domain;

import com.pinHouse.server.core.util.BirthDayUtil;
import com.pinHouse.server.security.oauth2.domain.OAuth2UserInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 사용할 유저 도메인 입니다.
 */
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
    private Gender gender;
    private LocalDate birthday;

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
                .birthday(BirthDayUtil.parseBirthday(userInfo.getBirthYear(), userInfo.getBirthday()))
                .gender(Gender.getGender(userInfo.getGender()))
                .role(Role.USER)
                .build();
    }

    /// 정적 팩토리 메서드
    public static User of(
            Provider provider, String socialId, String name, String email,
            String profileImage, String phoneNumber, LocalDate birthday, Gender gender
    ) {
        return User.builder()
                .id(UUID.randomUUID())
                .provider(provider)
                .socialId(socialId)
                .name(name)
                .email(email)
                .profileImage(profileImage)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .gender(gender)
                .role(Role.USER)
                .build();
    }


}
