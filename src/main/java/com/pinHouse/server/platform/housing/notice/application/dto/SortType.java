package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SortType {

    LATEST("최신공고순"),
    END("마감임박순");

    private final String label;

    @JsonValue
    public String getLabel() {
        return label;
    }

    public static SortType from(String source) {
        if (source == null) return null;
        String s = normalize(source);

        /// Enum.name() 매칭 허용 (LATEST, DEADLINE_ASC ...)
        for (SortType t : values()) {
            if (t.name().equalsIgnoreCase(s)) return t;
        }
        /// 한글 라벨 매칭 허용
        for (SortType t : values()) {
            if (normalize(t.label).equalsIgnoreCase(s)) return t;
        }
        throw new IllegalArgumentException("Invalid SortType: " + source);
    }

    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }
}
