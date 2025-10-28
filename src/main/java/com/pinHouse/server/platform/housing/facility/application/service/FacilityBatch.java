package com.pinHouse.server.platform.housing.facility.application.service;

import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class FacilityBatch {

    private final FacilityStatService statService;
    private final ComplexUseCase complexService;

    /// 다 저장하는 함수
    public void getFacilities() {

        /// 모든 아이디 가져오기
        List<ComplexDocument> documents = complexService.loadComplexes();

        documents.forEach(document -> {

            /// 좌표
            Double longitude = document.getLocation().getLongitude();
            Double latitude = document.getLocation().getLatitude();

            /// 인프라 개수 가져오기
            statService.getCountsOrRecompute(document.getComplexKey(), longitude, latitude);
        });
    }

}
