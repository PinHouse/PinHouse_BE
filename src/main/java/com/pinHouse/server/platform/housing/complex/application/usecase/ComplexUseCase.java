package com.pinHouse.server.platform.housing.complex.application.usecase;

import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexDetailResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.TransitRoutesResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.UnitTypeResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.like.application.dto.UnityTypeLikeResponse;
import com.pinHouse.server.platform.search.application.dto.ComplexDistanceResponse;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

public interface ComplexUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 상세 조회
    ComplexDetailResponse getComplex(String id, String pinPointId) throws UnsupportedEncodingException;;

    /// 상세 조회
    List<UnitTypeResponse> getComplexUnitTypes(String id, UUID userId);

    /**
     * 거리 시뮬레이터 전부 조회 (구 스키마)
     *
     * @deprecated 이 메서드는 구식 스키마를 사용합니다.
     *             대신 {@link #getDistanceV2(String, String)}를 사용하세요.
     *             이 메서드는 향후 버전에서 제거될 예정입니다.
     */
    @Deprecated(since = "1.0", forRemoval = true)
    List<DistanceResponse> getDistance(String id, String pinPointId) throws UnsupportedEncodingException;

    /// 거리 시뮬레이터 전부 조회 (새 스키마 - 3개 경로 한 번에)
    TransitRoutesResponse getDistanceV2(String id, String pinPointId) throws UnsupportedEncodingException;

    /// 간편 거리 시뮬레이터 (Redis 캐싱 포함)
    DistanceResponse getEasyDistance(String id, String pinPointId) throws UnsupportedEncodingException;

    /// 나의 좋아요 방 목록 조회
    List<UnityTypeLikeResponse> getComplexesLikes(UUID userId);


    // =================
    //  외부 로직
    // =================
    /// 상세 조회
    ComplexDocument loadComplex(String id);

    ComplexDocument loadComplexByUnitTypeId(String typeId);

    /// 공고 내부 목록 조회
    List<ComplexDocument> loadComplexes(String noticeId);

    /// 유닛타입 ID 목록으로 단지 목록 조회
    List<ComplexDocument> findComplexesByUnitTypeIds(List<String> typeIds);

    /// 거리 계산 필터링
    List<ComplexDistanceResponse> filterDistanceOnly(List<ComplexDocument> complexDocuments, SearchHistory req);

    /// 필터링
    List<ComplexDistanceResponse> filterUnitTypesOnly(List<ComplexDistanceResponse> filter, SearchHistory request);


}
