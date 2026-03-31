package com.pinHouse.domain.user.application.dto;

import com.pinHouse.common.util.BirthDayUtil;
import com.pinHouse.domain.housing.facility.domain.entity.FacilityType;
import com.pinHouse.domain.user.domain.entity.User;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MyPageResponse(
        UUID userId,
        String provider,
        String name,
        String nickName,
        String email,
        String phoneNumber,
        String role,
        String gender,
        String profileImage,
        String birthday,
        List<FacilityType> facilityTypes
) {

    /// 정적 팩토리 메서드
    public static MyPageResponse from(User user) {
        return MyPageResponse.builder()
                .userId(user.getId())
                .provider(user.getProvider().name())
                .nickName(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().getLabel())
                .gender(user.getGender() != null ? user.getGender().getValue() : "미정")
                .profileImage(user.getProfileImage())
                .birthday(BirthDayUtil.formatString(user.getBirthday()))
                .facilityTypes(user.getFacilityTypes())
                .build();
    }

}
