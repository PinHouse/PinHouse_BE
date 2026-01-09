package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * 유닛타입 정렬 기준
 */
@RequiredArgsConstructor
public enum UnitTypeSortType {
    DEPOSIT_ASC("보증금 낮은 순"),
    AREA_DESC("면적 넓은 순"),
    FACILITY_MATCH("주변환경 매칭 순"),
    DISTANCE_ASC("핀포인트 거리 순");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    /**
     * 문자열로부터 UnitTypeSortType 생성
     */
    public static UnitTypeSortType from(String source) {
        if (source == null) return DEPOSIT_ASC; // 기본값

        String normalized = normalize(source);

        // Enum.name() 매칭
        for (UnitTypeSortType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // 한글 라벨 매칭
        for (UnitTypeSortType type : values()) {
            if (normalize(type.label).equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        return DEPOSIT_ASC; // 기본값
    }

    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }
}
