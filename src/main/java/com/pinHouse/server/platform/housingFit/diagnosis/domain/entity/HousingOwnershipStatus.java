package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum HousingOwnershipStatus {
    /** 나는 무주택자지만 우리 가구원 중 주택 소유자가 있음 */
    HOUSEHOLD_MEMBER_OWNS_HOUSE("나는 무주택자지만 우리 가구원중 주택 소유자가 있어요"),

    /** 우리집 가구원 모두 주택을 소유하고 있지 않음 */
    NO_ONE_OWNS_HOUSE("우리집 가구원 모두 주택을 소유하고 있지 않아요"),

    /** 내가 주택을 소유하고 있음 */
    I_OWN_HOUSE("주택을 소유하고 있어요");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}
