package com.myhome.server.platform.application.in;

import com.myhome.server.core.response.response.pageable.PageRequest;
import com.myhome.server.platform.adapter.in.web.dto.response.NoticeDTO;
import com.myhome.server.platform.adapter.in.web.dto.response.NoticeSupplyDTO;
import com.myhome.server.platform.domain.notice.Notice;
import org.springframework.data.domain.Page;

import java.util.List;
/**
 * [공고 기반 조회] 인터페이스
 * - 최신 기반, 공고의 목록 조회
 * - 지역 필터링 기반, 공고 목록 조회
 * - 특정 공고 ID를 바탕으로 상세 조회
 * - 특정 공고 내부의 예산 정보를 바탕으로 [예산 시뮬레이터] 구현
 * - 특정 공고의 ID를 바탕으로 비교
 */

public interface NoticeUseCase {

    /// 조회
    // 최신 목록 조회
    Page<NoticeDTO.NoticeListResponse> getNotices(PageRequest pageRequest);

    // 지역 필터링 기반, 공고 목록 조회
    Page<NoticeDTO.NoticeListResponse> getNoticesByRegion(String region, PageRequest pageRequest);

    // 상세 조회
    NoticeDTO.NoticeDetailResponse getNoticeById(String noticeId);

    /// 시뮬레이터
    NoticeSupplyDTO.NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, String type, double percentage);

    /// 비교
    List<Notice> compareNotices(String noticeId1, String noticeId2);



}
