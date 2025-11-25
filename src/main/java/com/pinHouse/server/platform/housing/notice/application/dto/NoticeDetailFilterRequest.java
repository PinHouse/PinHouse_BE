package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import java.util.List;

public record NoticeDetailFilterRequest(

        DetailSortType sortType,

        @Schema(example = "fec9aba3-0fd9-4b75-bebf-9cb7641fd251")
        String pinPointId,

        @Schema(example = "100")
        int transitTime,

        @Schema(example = "[\"양주시\"]")
        List<String> region,

        @Schema(description = "대상 유형 목록", example = "[\"청년\", \"신혼부부\"]")
        List<NoticeListRequest.TargetType> targetType,

        @Schema(description = "보증금 최대값", example = "50000000")
        int maxDeposit,

        @Schema(description = "월 임대료 최대값", example = "300000")
        int maxMonthPay,

        @Schema(example = "[\"26A\"]")
        List<String> typeCode,

        @Schema(description = "원하는 인프라, 최대 3개까지 가능", example = "[\"공원\"]")
        @Size(max = 3)
        List<FacilityType> facilities

) {


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
