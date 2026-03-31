package com.pinHouse.security.jwt.application.dto;

import com.pinHouse.domain.user.domain.entity.Role;
import com.pinHouse.domain.user.domain.entity.User;
import lombok.Builder;

import java.util.UUID;

@Builder
public record JwtTokenRequest(
        UUID userId,
        Role role
) {

    /// 정적 팩토리 메서드
    public static JwtTokenRequest from(User user) {
        return JwtTokenRequest.builder()
                .userId(user.getId())
                .role(user.getRole())
                .build();
    }
}
