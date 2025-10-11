package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][검색] 빠른 검색 응답", description = "빠른 검색 결과를 위한 응답 DTO입니다.")
@Builder
public record FastSearchResponse(
        @Schema(description = "공고 ID", example = "N20231011-001")
        String noticeId,

        @Schema(description = "공고 이름", example = "2023년 가을 분양")
        String noticeName,

        @Schema(description = "공급 주체", example = "대한주택공사")
        String supplier,

        @Schema(description = "신청 기간", example = "2023-10-01 ~ 2023-10-31")
        String applicationPeriod,

        @Schema(description = "아파트 이름", example = "서울힐스테이트")
        String complexName,

        @Schema(description = "주소", example = "서울특별시 강남구 역삼동 123-45")
        String address,

        @Schema(description = "가격", example = "50000000")
        int price,

        @Schema(description = "평균 시간 (단위: 분)", example = "35.5")
        double averageTime
) {

    /// 정적 팩토리 메서드
    public static FastSearchResponse from(Notice notice, double averageTime) {
        return FastSearchResponse.builder()
                .noticeId(notice.getId())
                .noticeName(notice.getComplexName())
                .supplier(notice.getSupplier())
                .complexName(notice.getComplexName())
                .address(notice.getAddress())
                .price(0)
                .averageTime(averageTime)
                .build();
    }

}
