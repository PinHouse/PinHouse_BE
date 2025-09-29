package com.pinHouse.server.platform.user.domain.entity;

import com.pinHouse.server.core.util.BirthDayUtil;
import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.housing.facility.application.dto.request.FacilityType;
import com.pinHouse.server.security.oauth2.domain.OAuth2UserInfo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@AllArgsConstructor
@Builder
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String socialId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String profileImage;

    private LocalDate birthday;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "user_facility_types",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "facility_type")
    private List<FacilityType> facilityTypes;


    @PrePersist
    public void generateUUID() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

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
            String profileImage, String phoneNumber, LocalDate birthday, Gender gender, List<FacilityType> facilityTypes
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
                .facilityTypes(facilityTypes)
                .build();
    }

}
