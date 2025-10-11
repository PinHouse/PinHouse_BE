package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.core.util.DateUtil;
import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
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

        @Schema(description = "방타입 개수", example = "3")
        Integer rooms,

        @Schema(description = "공급유형", example = "영구임대")
        String type,

        @Schema(description = "모집일정", example = "2025년 10월 ~ 11월")
        String period
) {
    /// 정적 팩토리 메서드입니다.
    public static NoticeListResponse from(Notice notice) {

        /// 날짜
        String period = DateUtil.formatDate(notice.getStartDate(), notice.getEndDate());

        return NoticeListResponse.builder()
                .id(notice.getId())
                .name(notice.getTitle())
                .supplier(notice.getSupplier())
                .period(period)
                .type(notice.getType())
                .build();
    }

    /// 정적 팩토리 메서드입니다.
    public static List<NoticeListResponse> from(List<Notice> notices) {

        return notices.stream()
                .map(NoticeListResponse::from)
                .toList();
    }

}
