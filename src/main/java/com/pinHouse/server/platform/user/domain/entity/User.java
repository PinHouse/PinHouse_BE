package com.pinHouse.server.platform.user.domain.entity;

import com.pinHouse.server.core.util.NicknameUtil;
import com.pinHouse.server.platform.BaseTimeEntity;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(nullable = false)
    private String socialId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = true)
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

    /// 빌더 생성자
    @Builder
    protected User(UUID id,
                   Provider provider,
                   String socialId,
                   String name,
                   String nickname,
                   String email,
                   String phoneNumber,
                   Role role,
                   Gender gender,
                   String profileImage,
                   LocalDate birthday,
                   List<FacilityType> facilityTypes) {
        this.id = id;
        this.provider = provider;
        this.socialId = socialId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.gender = gender;
        this.profileImage = profileImage;
        this.birthday = birthday;
        this.facilityTypes = facilityTypes;
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
                .nickname(NicknameUtil.generateFromSocial(name, socialId))
                .email(email)
                .profileImage(profileImage)
                .phoneNumber(phoneNumber)
                .birthday(birthday)
                .gender(gender)
                .role(Role.USER)
                .facilityTypes(facilityTypes)
                .build();
    }

    /// 정적 팩토리 메서드
    public static User devOf(UUID id) {
        return User.builder()
                .id(id)
                .socialId("dev-naver-id")
                .email("pinhouse_naver@example.com")
                .profileImage("http://image-url")
                .phoneNumber("010-1111-1111")
                .name("naver개발자")
                .nickname("단단한집")
                .provider(Provider.NAVER)
                .role(Role.ADMIN)
                .birthday(LocalDate.now())
                .gender(Gender.Male)
                .facilityTypes(List.of())
                .build();
    }

    /// 비즈니스 로직
    /// 업데이트
    public void update(String imageUrl, String nickname) {

        if (imageUrl != null) {
            /// 프로필이미지를 수정할 내용이 존재한다면,
            this.profileImage = imageUrl;
        }

        if (nickname != null) {
            /// 닉네임을 수정할 내용이 존재한다면,
            this.nickname = nickname;
        }
    }
}
