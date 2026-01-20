package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Schema(name = "[요청][공고] 공고 상세 필터링 Request", description = "공고 상세 조회 시 필터링 조건을 지정하는 DTO")
public record NoticeDetailFilterRequest(

        @Schema(description = "정렬 유형 (거리순/생활태그 매칭순)", example = "거리 순")
        DetailSortType sortType,

        @NotBlank(message = "pinPointId는 필수 입력값입니다")
        @Schema(description = "핀포인트 ID (기준 위치)", example = "fec9aba3-0fd9-4b75-bebf-9cb7641fd251", required = true)
        String pinPointId,

        @Schema(description = "대중교통 최대 소요 시간 (분)", example = "100")
        int transitTime,

        @Schema(description = "지역 필터 (시/군/구)", example = "[\"양주시\"]")
        List<String> region,

        @Schema(description = "대상 유형 목록 (청년, 신혼부부 등)", example = "[\"청년\", \"신혼부부\"]")
        List<NoticeListRequest.TargetType> targetType,

        @Schema(description = "보증금 최대값 (원)", example = "50000000")
        int maxDeposit,

        @Schema(description = "월 임대료 최대값 (원)", example = "300000")
        int maxMonthPay,

        @Schema(description = "주택형 코드 필터", example = "[\"26A\"]")
        List<String> typeCode,

        @Schema(description = "원하는 인프라, 최대 3개까지 가능", example = "[\"문화센터\"]")
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
        INFRA("주변환경 매칭순");

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
