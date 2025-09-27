package com.pinHouse.server.platform.search.application.dto.response;

import com.pinHouse.server.platform.housing.notice.domain.entity.Notice;
import lombok.Builder;

/**
 *
 * @param noticeId              공고 ID
 * @param noticeName            공고 이름
 * @param supplier              공급주체
 * @param complexName           아파트 이름
 * @param address               주소
 * @param price                 가격
 * @param averageTime           평균시간
 */
@Builder
public record FastSearchResponse(
        String noticeId,
        String noticeName,
        String supplier,
        String applicationPeriod,
        String complexName,
        String address,
        int price,
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
