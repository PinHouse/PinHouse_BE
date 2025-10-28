package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][검색] 빠른 검색 응답", description = "빠른 검색 결과를 위한 응답 DTO입니다.")
@Builder
public record FastSearchResponse(

        @Schema(description = "임대주택 아이디", example = "2023년 가을 분양")
        String complexId,

        @Schema(description = "임대주택 이름", example = "서울힐스테이트")
        String complexName,

        @Schema(description = "임대주택 형", example = "26A")
        String typeCode,

        @Schema(description = "모집호수", example = "3")
        Integer quota,

        @Schema(description = "전용면적", example = "84.2")
        double exclusiveAreaM2,

        @Schema(description = "임대 보증금", example = "3000000")
        long deposit,

        @Schema(description = "월 임대료", example = "300000")
        int monthlyRent,

        @Schema(description = "평균 시간 (단위: 분)", example = "35.5")
        double averageTime
) {

        /// 정적 팩토리 메서드
        public static FastSearchResponse from(ComplexDocument complex, double avgAreaM2, long avgDeposit, int avgMonth, double averageTime) {

                return FastSearchResponse.builder()
                        .complexId(complex.getComplexKey())
                        .complexName(complex.getName())
                        .exclusiveAreaM2(avgAreaM2)
                        .deposit(avgDeposit)
                        .monthlyRent(avgMonth)
                        .averageTime(averageTime)
                        .build();
        }

}
