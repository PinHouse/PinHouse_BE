package com.pinHouse.server.platform.housing.facility.application.usecase;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;

import java.util.List;

/**
 * - 공고 주변의 인프라 목록 조회할 인터페이스
 */
public interface FacilityUseCase {

    /// 주변의 인프라 목록 조회
    NoticeFacilityListResponse getNearFacilities(String complexId);

    /// 원하는 인프라가 많은 임대주택 목록 조회
    List<ComplexDocument> getComplexes(List<FacilityType> facilityTypes);

    /// 외부 필터링 함수
    List<ComplexDocument> filterComplexesByFacility(List<NoticeDocument> noticeDocuments, List<FacilityType> facilityTypes);

    /// 주변의 인프라 목록 조회
    List<FacilityType> getFacilities(String complexId);


}
