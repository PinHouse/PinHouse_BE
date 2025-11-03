package com.pinHouse.server.platform.housing.complex.application.usecase;

import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DepositResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.like.application.dto.UnityTypeLikeResponse;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public interface ComplexUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 상세 조회
    ComplexDetailResponse getComplex(String id);

    /// 예산 시뮬레이터
    DepositResponse getLeaseByPercent(String id, String type, double percentage);

    /// 거리 시뮬레이터 간편 조회
    DistanceResponse getEasyDistance(String id, String pinPointId) throws UnsupportedEncodingException;

    /// 거리 시뮬레이터 전부 조회
    PathResult getDistance(String id, String pinPointId) throws UnsupportedEncodingException;

    /// 나의 좋아요 방 목록 조회
    List<UnityTypeLikeResponse> getComplexesLikes(UUID userId);


    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    ComplexDocument loadComplex(String id);

    ComplexDocument loadComplexByUnitTypeId(String typeId);

    /// 아이디 기반 조회
    List<ComplexDocument> loadComplexes(List<String> ids);

    /// 공고 내부 목록 조회
    List<ComplexDocument> loadComplexes(String noticeId);

    /// 거리 계산 필터링
    List<ComplexDocument> filterDistanceOnly(List<ComplexDocument> complexDocuments, FastSearchRequest req);

    /// 필터링
    List<UnitType> filterUnitTypesOnly(List<ComplexDocument> filter, FastSearchRequest request);


}
