package com.pinHouse.server.platform.user.application.dto;

import com.pinHouse.server.core.util.BirthDayUtil;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.FacilityType;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MyPageResponse(
        UUID userId,
        String provider,
        String socialId,
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
                .socialId(user.getSocialId())
                .nickName(user.getNickname())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().getLabel())
                .gender(user.getGender().getValue())
                .profileImage(user.getProfileImage())
                .birthday(BirthDayUtil.formatString(user.getBirthday()))
                .facilityTypes(user.getFacilityTypes())
                .build();
    }

}
