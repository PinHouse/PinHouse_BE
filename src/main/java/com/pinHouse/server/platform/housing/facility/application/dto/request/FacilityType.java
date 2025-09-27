package com.pinHouse.server.platform.housing.facility.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.response.response.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FacilityType {

    LIBRARY("도서관"),       // 도서관
    PARK("공원"),            // 공원
    ANIMAL("동물 관련시설"),   // 동물 관련 시설
    WALKING("산책로"),        // 산책로
    SPORT("스포츠 시설"),       // 스포츠 시설
    STORE("대형점포"),      // 대형점포
    HOSPITAL("병원"),   // 병원
    EXHIBITION("전시회"), // 전시회
    LAUNDRY("빨래방");     // 빨래방
    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FacilityType fromValue(String value) {
        for (FacilityType facilityType : FacilityType.values()) {
            if (facilityType.getValue().equals(value)) {
                return facilityType;
            }
        }
        throw new IllegalArgumentException(ErrorCode.INVALID_INPUT.getMessage());
    }
}
