package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * 유닛타입(방) 비교 요청 DTO
 */
@Schema(name = "[요청][공고] 유닛타입 비교", description = "공고의 모든 유닛타입을 비교하기 위한 요청")
public record UnitTypeCompareRequest(
        @Schema(description = "공고 ID", example = "19362")
        @NotBlank(message = "공고 ID는 필수입니다")
        String noticeId,

        @Schema(description = "정렬 기준 (기본값: DEPOSIT_ASC)", example = "DEPOSIT_ASC")
        UnitTypeSortType sortType
) {

    /**
     * 유닛타입 정렬 기준
     */
    @RequiredArgsConstructor
    public enum UnitTypeSortType {
        DEPOSIT_ASC("보증금 낮은 순"),
        AREA_DESC("평수 넓은 순");

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
}
