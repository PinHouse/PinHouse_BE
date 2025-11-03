package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "[응답][공고] 공고 목록 조회 응답", description = "공고 목록 조회를 위한 DTO입니다.")
@Builder
public record NoticeListResponse(

        @Schema(description = "공고ID", example = "101")
        String id,

        @Schema(description = "공고 썸네일 URL", example = "https://example.jpg")
        String thumbnailUrl,

        @Schema(description = "공고명", example = "2025 청년 행복주택 공고")
        String name,

        @Schema(description = "공급주체", example = "LH")
        String supplier,

        @Schema(description = "공급 주택 개수", example = "3")
        Integer complexes,

        @Schema(description = "공급유형", example = "영구임대")
        String type,

        @Schema(description = "모집 일정", example = "2025년 10월 ~ 11월")
        String applyPeriod,

        @Schema(description = "좋아요 여부", example = "false")
        boolean liked

        ) {

    /// 정적 팩토리 메서드입니다.
    public static NoticeListResponse from(NoticeDocument notice, boolean liked) {

        /// 날짜
        String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());

        return NoticeListResponse.builder()
                .id(notice.getNoticeId())
                .name(notice.getTitle())
                .supplier(notice.getAgency())
                .complexes(notice.getMeta().getTotalComplexCount())
                .applyPeriod(period)
                .type(notice.getSupplyType())
                .liked(liked)
                .build();
    }

    /// 정적 팩토리 메서드입니다.
    public static NoticeListResponse from(NoticeDocument notice) {

        /// 날짜
        String applyPeriod = notice.getApplyStart() + "~" + notice.getApplyEnd();

        return NoticeListResponse.builder()
                .id(notice.getNoticeId())
                .name(notice.getTitle())
                .supplier(notice.getAgency())
                .complexes(notice.getMeta().getTotalComplexCount())
                .applyPeriod(applyPeriod)
                .type(notice.getSupplyType())
                .liked(true)
                .build();
    }

    /// 정적 팩토리 메서드입니다.
    public static List<NoticeListResponse> from(List<NoticeDocument> notices) {

        return notices.stream()
                .map(NoticeListResponse::from)
                .toList();
    }

}
