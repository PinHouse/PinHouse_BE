package com.pinHouse.server.platform.search.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * 공고 검색 상태 필터
 */
@RequiredArgsConstructor
public enum NoticeSearchFilterType {

    ALL("전체"),
    RECRUITING("모집중");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    /**
     * String을 NoticeSearchFilterType으로 변환
     * Enum 이름 또는 한글 라벨 모두 지원
     */
    public static NoticeSearchFilterType from(String source) {
        if (source == null) {
            return ALL; // 기본값
        }

        String normalized = normalize(source);

        // Enum.name() 매칭 (ALL, RECRUITING)
        for (NoticeSearchFilterType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // 한글 라벨 매칭 (전체, 모집중)
        for (NoticeSearchFilterType type : values()) {
            if (normalize(type.label).equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // 기본값 반환
        return ALL;
    }

    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }
}
