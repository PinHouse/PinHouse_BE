package com.pinHouse.server.platform.housing.complex.application;

import com.pinHouse.server.platform.housing.complex.application.dto.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.application.dto.DepositResponse;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface ComplexUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 예산 시뮬레이터
    DepositResponse getLeaseByPercent(String id, String type, double percentage);

    /// 거리 시뮬레이터
    List<DistanceResponse> getDistance(String id, Long pinPointId) throws UnsupportedEncodingException;

    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    ComplexDocument loadComplex(String id);

    /// 전체 목록 조회
    List<ComplexDocument> loadComplexes();

    /// 공고 내부 목록 조회
    List<ComplexDocument> loadComplexes(String noticeId);
}
