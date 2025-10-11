package com.pinHouse.server.platform.housing.notice.application.dto;

import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(name = "[응답][공고] 공고 목록 조회 응답", description = "공고 목록 조회를 위한 DTO입니다.")
@Builder
public record NoticeListResponse(
        @Schema(description = "공고ID", example = "101")
        String noticeId,

        @Schema(description = "공고명", example = "2025년 하반기 주택 공급 공고")
        String noticeName,

        @Schema(description = "공급기관명", example = "국토교통부")
        String supplier,

        @Schema(description = "모집시기", example = "2025년 10월 ~ 11월")
        String applicationPeriod,

        @Schema(description = "단지 이름", example = "한강자이 아파트")
        String complexName,

        @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
        String address)
{
    /// 정적 팩토리 메서드입니다.
    public static NoticeListResponse from(Notice notice) {
        return NoticeListResponse.builder()
                .noticeId(notice.getId())
                .noticeName(notice.getTitle())
                .supplier(notice.getSupplier())
                .applicationPeriod(notice.getStartDate())
                .complexName(notice.getComplexName())
                .address(notice.getAddress())
                .build();
    }

    /// 정적 팩토리 메서드입니다.
    public static List<NoticeListResponse> from(List<Notice> notices) {
        return notices.stream()
                .map(NoticeListResponse::from)
                .toList();
    }

}
