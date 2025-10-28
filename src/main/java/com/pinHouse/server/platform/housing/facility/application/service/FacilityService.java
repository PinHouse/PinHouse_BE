package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService implements FacilityUseCase {

    /// 공고 의존성
    private final ComplexUseCase complexService;

    /// 인프라 의존성
    private final FacilityStatService statsService;


    // =================
    //  퍼블릭 로직
    // =================

    /// 주변의 인프라 목록 조회
    public NoticeFacilityListResponse getFacilities(String complexId) {

        /// 임대주택 예외처리
        ComplexDocument notice = complexService.loadComplex(complexId);

        double lng = notice.getLocation().getLongitude();
        double lat = notice.getLocation().getLatitude();

        /// 인프라 개수 가져오기
        Map<FacilityType, Integer> response = statsService.getCountsOrRecompute(complexId, lng, lat);

        /// 리턴
        return NoticeFacilityListResponse.from(response);
    }

    /// 특정 인프라가 많은 곳 조회
    @Override
    public List<ComplexDocument> getComplexes(List<FacilityType> facilityTypes) {

        /// 도메인 조회
        List<FacilityStatDocument> types = statsService.findByAllTypesOver(facilityTypes, 3);

        /// 임대주택 아이디 모음
        List<String> complexIds = types.stream()
                .map(FacilityStatDocument::getId)
                .toList();

        /// 임대주택 체크
        return complexService.loadComplexes(complexIds);
    }

    // =================
    //  내부 함수
    // =================

}
