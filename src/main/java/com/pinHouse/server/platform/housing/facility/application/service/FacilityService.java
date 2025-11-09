package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
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
    public NoticeFacilityListResponse getNearFacilities(String complexId) {

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
    //  외부 함수
    // =================
    /// 특정 인프라가 많은 곳 조회
    @Override
    public List<ComplexDocument> filterComplexesByFacility(List<NoticeDocument> noticeDocuments, List<FacilityType> facilityTypes) {

        /// 공고 ID 추출
        List<String> noticeIds = noticeDocuments.stream()
                .map(NoticeDocument::getNoticeId)
                .toList();

        /// 시설 통계 조회 (예: 주변 3개 이상)
        List<FacilityStatDocument> types = statsService.findByAllTypesOver(facilityTypes, 3);

        /// 시설 통계 → 단지 ID 리스트 추출
        List<String> complexIds = types.stream()
                .map(FacilityStatDocument::getId)
                .toList();

        /// 단지 조회
        List<ComplexDocument> complexes = complexService.loadComplexes(complexIds);

        /// noticeId가 일치하는 단지만 필터링
        return complexes.stream()
                .filter(c -> noticeIds.contains(c.getNoticeId()))
                .toList();
    }


    @Override
    public List<FacilityType> getFacilities(String complexId) {
        /// 임대주택 예외처리
        ComplexDocument notice = complexService.loadComplex(complexId);

        double lng = notice.getLocation().getLongitude();
        double lat = notice.getLocation().getLatitude();

        /// 인프라 개수 가져오기
        Map<FacilityType, Integer> response = statsService.getCountsOrRecompute(complexId, lng, lat);

        /// 3개 이상인 FacilityType
        return response.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue() >= 3)
                .map(Map.Entry::getKey)
                .toList();
    }


}
