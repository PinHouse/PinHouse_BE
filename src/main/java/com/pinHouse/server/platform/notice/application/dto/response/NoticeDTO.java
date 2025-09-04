package com.pinHouse.server.platform.notice.application.dto.response;

import com.pinHouse.server.platform.notice.domain.Notice;
import lombok.Builder;

import java.util.List;

public record NoticeDTO() {

    /**
     * 공고 목록 정보를 위한 DTO 입니다.
     * @param noticeId              공고ID
     * @param noticeName            공고명
     * @param supplier              공급기관명,
     * @param applicationPeriod     모집시기
     * @param complexName           단지 이름
     * @param address               주소
     */

    @Builder
    public record NoticeListResponse(
            String noticeId,
            String noticeName,
            String supplier,
            String applicationPeriod,
            String complexName,
            String address) {

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
                    .map(NoticeDTO.NoticeListResponse::from)
                    .toList();
        }

    }

    /**
     * 공고 상세 정보를 위한 DTO 입니다.
     * @param noticeId              공고ID
     * @param noticeName            공고명
     * @param supplier              공급기관명,
     * @param applicationPeriod     모집시기
     * @param complexName           단지 이름
     * @param address               주소
     * @param latitude              위도
     * @param longitude             경도
     * @param householdCount        총세대수
     * @param heatingType           난방방식
     * @param expectedMoveInDate    당첨자발표일자
     */
    @Builder
    public record NoticeDetailResponse(
            String noticeId,
            String noticeName,
            String supplier,
            String applicationPeriod,
            String complexName,
            String address,
            Double latitude,
            Double longitude,
            String householdCount,
            String heatingType,
            String expectedMoveInDate,
            List<NoticeSupplyDTO.NoticeSupplyResponse> supply
    ) {

        /// 정적 팩토리 메서드입니다.
        public static NoticeDetailResponse from(Notice notice) {
            return NoticeDetailResponse.builder()
                    .noticeId(notice.getId())
                    .noticeName(notice.getTitle())
                    .supplier(notice.getSupplier())
                    .applicationPeriod(notice.getStartDate())
                    .complexName(notice.getComplexName())
                    .address(notice.getAddress())
                    .latitude(notice.getLocation().getLatitude())
                    .longitude(notice.getLocation().getLongitude())
                    .householdCount(notice.getTotalHouseholds())
                    .heatingType(notice.getHeatingMethod())
                    .expectedMoveInDate(notice.getEndDate())
                    .supply(notice.getSupplyInfo().stream()
                            .map(NoticeSupplyDTO.NoticeSupplyResponse::from)
                            .toList())
                    .build();

        }

    }
}

