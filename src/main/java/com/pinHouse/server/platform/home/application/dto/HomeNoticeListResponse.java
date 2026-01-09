package com.pinHouse.server.platform.home.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(name = "[응답][홈] 홈 화면 공고 목록 조회 응답", description = "홈 화면에서 마감임박 공고 목록을 조회하기 위한 DTO입니다.")
@Builder
public record HomeNoticeListResponse(

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
        boolean liked,

        @Schema(description = "공통 지역", example = "성남시")
        String region

) {

    /**
     * NoticeDocument와 좋아요 여부, 지역 정보로부터 HomeNoticeListResponse를 생성
     */
    public static HomeNoticeListResponse from(NoticeDocument notice, boolean liked, String region) {
        String period = DateUtil.formatDate(notice.getApplyStart(), notice.getApplyEnd());

        return HomeNoticeListResponse.builder()
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
                .region(region)
                .build();
    }
}
