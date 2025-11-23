package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

/**
 * 공고 검색 결과 단일 항목 응답 DTO
 */
@Builder
@Schema(name = "[응답][검색] 공고 검색 결과", description = "공고 검색 결과 단일 항목")
public record NoticeSearchResultResponse(
        @Schema(description = "공고 ID", example = "18442")
        String id,

        @Schema(description = "공고 제목", example = "2024년 행복주택 입주자 모집공고")
        String title,

        @Schema(description = "공급 기관", example = "LH 한국토지주택공사")
        String agency,

        @Schema(description = "주택 유형", example = "공공임대")
        String housingType,

        @Schema(description = "공급 유형", example = "행복주택")
        String supplyType,

        @Schema(description = "공고일", example = "2024-01-15")
        LocalDate announceDate,

        @Schema(description = "신청 시작일", example = "2024-01-20")
        LocalDate applyStart,

        @Schema(description = "신청 종료일", example = "2024-01-24")
        LocalDate applyEnd,

        @Schema(description = "좋아요 여부", example = "true")
        boolean liked
) {

    /**
     * NoticeDocument에서 NoticeSearchResultResponse 생성
     */
    public static NoticeSearchResultResponse from(NoticeDocument notice, boolean liked) {
        return NoticeSearchResultResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .agency(notice.getAgency())
                .housingType(notice.getHouseType())
                .supplyType(notice.getSupplyType())
                .announceDate(notice.getAnnounceDate())
                .applyStart(notice.getApplyStart())
                .applyEnd(notice.getApplyEnd())
                .liked(liked)
                .build();
    }

    /**
     * NoticeDocument에서 NoticeSearchResultResponse 생성 (좋아요 정보 없음)
     */
    public static NoticeSearchResultResponse from(NoticeDocument notice) {
        return from(notice, false);
    }
}
