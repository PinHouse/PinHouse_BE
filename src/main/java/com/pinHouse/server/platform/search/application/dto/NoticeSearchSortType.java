package com.pinHouse.server.platform.search.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

/**
 * 공고 검색 정렬 타입
 */
@RequiredArgsConstructor
public enum NoticeSearchSortType {

    LATEST("최신공고순"),
    END("마감임박순");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    /**
     * String을 NoticeSearchSortType으로 변환
     * Enum 이름 또는 한글 라벨 모두 지원
     */
    public static NoticeSearchSortType from(String source) {
        if (source == null) {
            return LATEST; // 기본값
        }

        String normalized = normalize(source);

        // Enum.name() 매칭 (LATEST, END)
        for (NoticeSearchSortType type : values()) {
            if (type.name().equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // 한글 라벨 매칭 (최신공고순, 마감임박순)
        for (NoticeSearchSortType type : values()) {
            if (normalize(type.label).equalsIgnoreCase(normalized)) {
                return type;
            }
        }

        // 기본값 반환
        return LATEST;
    }

    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }
}
