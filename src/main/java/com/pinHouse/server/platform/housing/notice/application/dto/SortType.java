package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

public class SortType {

    // =================
    //  ListSortType 퍼블릭 로직
    // =================

    /// 목록 조회를 위한 정렬 파라미터
    @RequiredArgsConstructor
    public enum ListSortType {

        LATEST("최신공고순"),
        END("마감임박순");

        private final String label;

        @JsonValue
        public String getLabel() {
            return label;
        }

        public static ListSortType from(String source) {
            if (source == null) return null;
            String s = normalize(source);

            /// Enum.name() 매칭 허용 (LATEST, DEADLINE_ASC ...)
            for (ListSortType t : values()) {
                if (t.name().equalsIgnoreCase(s)) return t;
            }
            /// 한글 라벨 매칭 허용
            for (ListSortType t : values()) {
                if (normalize(t.label).equalsIgnoreCase(s)) return t;
            }
            throw new IllegalArgumentException("Invalid ListSortType: " + source);
        }
    }

    // =================
    //  DetailSortType 퍼블릭 로직
    // =================

    /// 상세 조회를 위한 정렬 파라미터
    @RequiredArgsConstructor
    public enum DetailSortType {

        KM("거리 순"),
        INFRA("생활태그 매칭순");

        private final String label;

        @JsonValue
        public String getLabel() {
            return label;
        }

        /// 생성기
        public static DetailSortType from(String source) {
            if (source == null) return null;
            String s = normalize(source);

            /// Enum.name() 매칭 허용 (LATEST, DEADLINE_ASC ...)
            for (DetailSortType t : values()) {
                if (t.name().equalsIgnoreCase(s)) return t;
            }
            /// 한글 라벨 매칭 허용
            for (DetailSortType t : values()) {
                if (normalize(t.label).equalsIgnoreCase(s)) return t;
            }
            throw new IllegalArgumentException("Invalid DetailSortType: " + source);
        }

    }

    // =================
    //  내부 로직
    // =================
    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }


}
