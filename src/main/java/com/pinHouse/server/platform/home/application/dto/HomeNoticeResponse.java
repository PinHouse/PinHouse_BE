package com.pinHouse.server.platform.home.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][홈] 개별 공고 정보", description = "홈 화면의 개별 공고 정보입니다.")
@Builder
public record HomeNoticeResponse(

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

        @Schema(description = "주택유형", example = "아파트")
        String housingType,

        @Schema(description = "모집 공고일", example = "2025년 10월 5일")
        String announcePeriod,

        @Schema(description = "모집 일정", example = "2025년 10월 3일 ~ 11월 3일")
        String applyPeriod,

        @Schema(description = "좋아요 여부", example = "false")
        boolean liked

) {

    /**
     * NoticeDocument와 좋아요 여부로부터 HomeNoticeResponse를 생성
     */
    public static HomeNoticeResponse from(NoticeDocument notice, boolean liked) {
        String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());

        return HomeNoticeResponse.builder()
                .id(notice.getId())
                .thumbnailUrl(notice.getThumbnail())
                .name(notice.getTitle())
                .supplier(notice.getAgency())
                .complexes(notice.getMeta().getTotalComplexCount())
                .announcePeriod(notice.getAnnounceDate().toString())
                .applyPeriod(period)
                .type(notice.getSupplyType())
                .housingType(notice.getHouseType())
                .liked(liked)
                .build();
    }
}
