package com.pinHouse.server.platform.facility.application.usecase;

import com.pinHouse.server.platform.facility.application.dto.request.FacilityType;
import com.pinHouse.server.platform.notice.domain.entity.Notice;
import com.pinHouse.server.platform.facility.application.dto.response.FacilityResponse;

import java.util.List;

/**
 * - 공고 주변의 인프라 목록 조회할 인터페이스
 */
public interface FacilityUseCase {

    /// 조회
    // 주변의 인프라 개수 조회
    FacilityResponse getNoticeInfraById(String noticeId);

    // 원하는 인프라 바탕으로 많이 존재하는 공고 조회
    List<Notice> getNoticesByInfraTypesWithAllMinCount(List<FacilityType> facilityTypes);
}
