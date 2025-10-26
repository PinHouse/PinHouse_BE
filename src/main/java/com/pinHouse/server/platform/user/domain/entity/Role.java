package com.pinHouse.server.platform.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.UserErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("유저"),
    ADMIN("관리자");

    private final String label;

    /// ROLE 가져올 때
    public String getRole() {
        return "ROLE_" + name();
    }

    /// JSON에서 응답
    @JsonValue
    public String getLabel() {
        return label;
    }

    /// JSON에서 생성
    @JsonCreator
    public static Role fromLabel(String label) {
        for (Role userRole : values()) {
            if (userRole.label.equals(label)) {
                return userRole;
            }
        }
        throw new CustomException(UserErrorCode.BAD_REQUEST_ROLE);
    }

}
