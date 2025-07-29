package com.pinHouse.server.platform.adapter.out.jpa.user;

import com.pinHouse.server.platform.adapter.out.jpa.BaseTimeEntity;
import com.pinHouse.server.platform.domain.user.Gender;
import com.pinHouse.server.platform.domain.user.Provider;
import com.pinHouse.server.platform.domain.user.Role;
import com.pinHouse.server.platform.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@AllArgsConstructor
@Builder
public class UserJpaEntity extends BaseTimeEntity {

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

    @PrePersist
    public void generateUUID() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public static UserJpaEntity from(User user) {
        return UserJpaEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .provider(user.getProvider())
                .socialId(user.getSocialId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .gender(user.getGender())
                .profileImage(user.getProfileImage())
                .birthday(user.getBirthday())
                .build();
    }

    public User toDomain() {
        return User.builder()
                .id(id)
                .provider(provider)
                .socialId(socialId)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .role(role)
                .gender(gender)
                .profileImage(profileImage)
                .birthday(birthday)
                .build();
    }

}
