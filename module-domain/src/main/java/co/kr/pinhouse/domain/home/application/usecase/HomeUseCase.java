package co.kr.pinhouse.domain.home.application.usecase;

import java.util.List;
import java.util.UUID;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.domain.home.application.dto.HomeNoticeListResponse;
import co.kr.pinhouse.domain.home.application.dto.HomeSearchCategoryPageResponse;
import co.kr.pinhouse.domain.home.application.dto.HomeSearchCategoryType;
import co.kr.pinhouse.domain.home.application.dto.HomeSearchOverviewResponse;
import co.kr.pinhouse.domain.home.application.dto.NoticeCountResponse;
import co.kr.pinhouse.domain.search.application.dto.PopularKeywordResponse;

/**
 * 홈 화면 Use Case
 */
public interface HomeUseCase {

	/**
	 * 마감임박공고 조회 (PinPoint 지역 기반)
	 * @param pinpointId PinPoint ID (해당 지역의 공고를 조회)
	 * @param sliceRequest 페이징 정보
	 * @param userId 사용자 ID (좋아요 정보 조회용, null 가능)
	 * @return 해당 지역의 마감임박순으로 정렬된 공고 목록 (region + notices 배열)
	 */
	HomeNoticeListResponse getDeadlineApproachingNotices(
			String pinpointId,
			SliceRequest sliceRequest,
			UUID userId
	);

	/**
	 * 핀포인트 기준 최대 이동 시간 내 공고 개수 조회
	 * @param pinPointId 핀포인트 ID (기준 위치)
	 * @param maxTime 최대 이동 시간 (분)
	 * @param userId 사용자 ID (PinPoint 소유권 검증용)
	 * @return 공고 개수
	 */
	NoticeCountResponse getNoticeCountWithinTravelTime(String pinPointId, int maxTime, UUID userId);

	/**
	 * 진단 기반 추천 공고 조회
	 * 사용자의 최근 청약 진단 결과를 기반으로 맞춤형 공고를 추천
	 *
	 * @param sliceRequest 페이징 정보 (page, offSet)
	 * @param userId 사용자 ID (진단 결과 조회 및 좋아요 정보용)
	 * @return 진단 기반 추천 공고 목록 (마감임박순 정렬, 모든 공고 상태 포함)
	 */
	HomeNoticeListResponse getRecommendedNoticesByDiagnosis(
			SliceRequest sliceRequest,
			UUID userId
	);

	/**
	 * 홈 통합 검색 미리보기 (섹션별 5개)
	 */
	HomeSearchOverviewResponse searchHomeOverview(String keyword, UUID userId);

	/**
	 * 홈 통합 검색 카테고리별 조회
	 */
	HomeSearchCategoryPageResponse searchHomeByCategory(
			HomeSearchCategoryType category,
			String keyword,
			int page,
			UUID userId
	);

	/**
	 * 홈 인기 검색어 조회
	 */
	List<PopularKeywordResponse> getHomePopularKeywords(int limit);
}
