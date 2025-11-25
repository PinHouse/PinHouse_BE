package com.pinHouse.server.platform.housing.notice.application.usecase;

import com.pinHouse.server.core.response.response.pageable.SliceRequest;
import com.pinHouse.server.core.response.response.pageable.SliceResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.ComplexFilterResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilterRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeDetailFilteredResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeCompareResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.search.domain.entity.SearchHistory;

import java.util.List;
import java.util.UUID;

public interface NoticeUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 공고 목록 조회 (필터링과 함께)
    SliceResponse<NoticeListResponse> getNotices(NoticeListRequest request, SliceRequest sliceRequest, UUID userId);

    /// 개수 파악하기
    Long countNotices(NoticeListRequest request);

    /// 공고 상세 조회 (필터 적용 - filtered/nonFiltered 분리)
    NoticeDetailFilteredResponse getNotice(String noticeId, NoticeDetailFilterRequest request);

    /// 공고의 단지 필터링 정보 조회 (지역, 가격, 면적)
    ComplexFilterResponse getComplexFilters(String noticeId);

    /// 유닛타입(방) 비교
    UnitTypeCompareResponse compareUnitTypes(String noticeId, String pinPointId, UnitTypeSortType sortType);

    /// 나의 좋아요 공고 목록 조회
    List<NoticeListResponse> getNoticesLike(UUID userId);

    // =================
    //  외부 로직
    // =================

    /// 상세 조회
    NoticeDocument loadNotice(String id);

    /// 필터링을 위한 함수
    List<NoticeDocument> filterNotices(SearchHistory request);

}
