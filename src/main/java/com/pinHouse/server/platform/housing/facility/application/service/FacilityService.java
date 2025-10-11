package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.complex.application.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacility;
import com.pinHouse.server.platform.housing.facility.application.usecase.FacilityUseCase;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.*;
import com.pinHouse.server.platform.housing.facility.domain.repository.FacilityRepositoryRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.CastUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityService implements FacilityUseCase {

    /// 공고 의존성
    private final ComplexUseCase complexService;

    /// 인프라 의존성
    private final FacilityRepositoryRegistry registry;

    /// 상수
    private final double radiusKm = 1;
    private final double RADIUS_IN_RAD = radiusKm / 6371.0;

    // =================
    //  퍼블릭 로직
    // =================

    /// 주변의 인프라 목록 조회
    public NoticeFacilityListResponse getFacilities(String complexId) {

        /// 임대주택 예외처리
        ComplexDocument notice = complexService.loadComplex(complexId);

        double lng = notice.getLocation().getLongitude();
        double lat = notice.getLocation().getLatitude();

        /// 인프라 전부 가져오기
        List<Library> libraries = CastUtils.cast(registry.get(FacilityType.LIBRARY)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Animal> animals = CastUtils.cast(registry.get(FacilityType.ANIMAL)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Park> parks = CastUtils.cast(registry.get(FacilityType.PARK)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Walking> walkings = CastUtils.cast(registry.get(FacilityType.WALKING)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Sport> sports = CastUtils.cast(registry.get(FacilityType.SPORT)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Exhibition> exhibitions = CastUtils.cast(registry.get(FacilityType.EXHIBITION)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Laundry> laundries = CastUtils.cast(registry.get(FacilityType.LAUNDRY)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Hospital> hospitals = CastUtils.cast(registry.get(FacilityType.HOSPITAL)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        List<Mart> marts = CastUtils.cast(registry.get(FacilityType.STORE)
                .findByLocation(lng, lat, RADIUS_IN_RAD));

        /// DTO 변환을 위해서 정읜
        NoticeFacility facility = NoticeFacility.of(
                notice, libraries, animals, sports, walkings, parks, exhibitions, laundries, hospitals, marts);

        /// 리턴
        return NoticeFacilityListResponse.from(facility);
    }

    /// 특정 인프라가 많은 곳 조회
    @Override
    public List<ComplexDocument> getComplexes(List<FacilityType> facilityTypes) {
        List<ComplexDocument> documents = complexService.loadComplexes();

        return documents.stream()
                .filter(doc -> {
                    double lng = doc.getLocation().getLongitude();
                    double lat = doc.getLocation().getLatitude();

                    return facilityTypes.stream().allMatch(type -> {
                        long count = registry.get(type).countByLocation(lng, lat, RADIUS_IN_RAD);
                        return count >= 2;
                    });
                })
                .toList();
    }

    // =================
    //  내부 함수
    // =================

}
