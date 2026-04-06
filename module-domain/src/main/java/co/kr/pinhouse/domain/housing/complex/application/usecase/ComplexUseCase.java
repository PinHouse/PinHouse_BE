package co.kr.pinhouse.domain.housing.complex.application.usecase;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.domain.housing.complex.application.dto.response.ComplexDetailResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.DistanceResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.TransitRoutesResponse;
import co.kr.pinhouse.domain.housing.complex.application.dto.response.UnitTypeResponse;
import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;
import co.kr.pinhouse.domain.like.application.dto.UnityTypeLikeResponse;
import co.kr.pinhouse.domain.search.application.dto.ComplexDistanceResponse;
import co.kr.pinhouse.domain.search.domain.entity.SearchHistory;

public interface ComplexUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 상세 조회
	ComplexDetailResponse getComplex(String id, String pinPointId) throws UnsupportedEncodingException;

	/// 상세 조회
	List<UnitTypeResponse> getComplexUnitTypes(String id, UUID userId);

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

	/// 공고 내부 목록 조회 (정렬된 유닛타입 포함)
	List<ComplexDocument> loadSortedComplexes(String noticeId,
		co.kr.pinhouse.domain.housing.notice.application.dto.UnitTypeSortType sortType);

	/// 유닛타입 ID 목록으로 단지 목록 조회
	List<ComplexDocument> findComplexesByUnitTypeIds(List<String> typeIds);

	/// 거리 계산 필터링
	List<ComplexDistanceResponse> filterDistanceOnly(List<ComplexDocument> complexDocuments, SearchHistory req);

	/// 필터링
	List<ComplexDistanceResponse> filterUnitTypesOnly(List<ComplexDistanceResponse> filter, SearchHistory request);

}
