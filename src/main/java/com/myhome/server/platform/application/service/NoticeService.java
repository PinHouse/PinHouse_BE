package com.myhome.server.platform.application.service;

import com.myhome.server.core.response.response.ErrorCode;
import com.myhome.server.core.response.response.pageable.PageRequest;
import com.myhome.server.platform.adapter.in.web.dto.response.NoticeDTO;
import com.myhome.server.platform.adapter.in.web.dto.response.NoticeLeaseOptionResponse;
import com.myhome.server.platform.application.in.NoticeUseCase;
import com.myhome.server.platform.application.out.notice.NoticePort;
import com.myhome.server.platform.domain.notice.Notice;
import com.myhome.server.platform.domain.notice.NoticeSupplyInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService implements NoticeUseCase {

    private final NoticePort port;

    /**
     * 최신 공고 목록 조회를 위한 함수 입니다.
     * @param pageRequest  페이지 기반
     */
    @Override
    public Page<NoticeDTO.NoticeListResponse> getNotices(PageRequest pageRequest) {

        /// 페이징 변환
        Pageable pageable = getPageable(pageRequest);

        /// port 조회
        Page<Notice> notices = port.loadNotices(pageable);

        /// DTO 변환
        List<Notice> contents = notices.getContent();
        List<NoticeDTO.NoticeListResponse> responseList = NoticeDTO.NoticeListResponse.from(contents);

        return new PageImpl<>(responseList, pageable, contents.size());
    }

    /**
     * 지역 기반 공고 목록 조회를 위한 함수 입니다.
     * @param region    지역
     * @param pageRequest  페이지 기반
     */
    @Override
    public Page<NoticeDTO.NoticeListResponse> getNoticesByRegion(String region, PageRequest pageRequest) {

        /// 페이징 변환
        Pageable pageable = getPageable(pageRequest);

        return null;
    }

    /**
     * 공고 상세 조회를 위한 함수 입니다.
     * @param noticeId  조회할 공고 ID
     */
    @Override
    public NoticeDTO.NoticeDetailResponse getNoticeById(String noticeId) {

        /// 공고 조회
        Notice notice = getNotice(noticeId);

        /// DTO 변환
        return NoticeDTO.NoticeDetailResponse.from(notice);
    }


    /**
     * 상세 공고 내부에서 예산의 비율을 조절하는 함수 입니다.
     * @param noticeId      조회할 공고 ID
     * @param percentage    전환할 보증금 퍼센트
     */
    @Override
    public NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, double percentage) {

        /// 공고 예외처리
        Notice notice = getNotice(noticeId);

        /// 공고에서 예산 데이터 가져오기
        List<NoticeSupplyInfo> supplyInfo = notice.getSupplyInfo();

        /// 임대보증금과 월 임대료 전환 계산

        return null;
    }

    /**
     * 사용자가 원하는 공고 목록을 비교할 함수 입니다.
     * @param noticeId1     비교할 첫번째 공고 ID
     * @param noticeId2     비교할 두번째 공고 ID
     */
    @Override
    public List<Notice> compareNotices(String noticeId1, String noticeId2) {
        return List.of();
    }



    /// 공통 함수 모음

    /**
     * - ID를 바탕으로 공고를 조회하는 함수입니다.
     * @param noticeId  공고 ID
     * @return          Notice 객체
     */
    private Notice getNotice(String noticeId) {
        return port.loadById(noticeId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }


    /**
     * 페이지 요청을 Pageable 변환하는 함수 입니다.
     * @param pageRequest   페이지 요청 DTO
     * @return              Pageable
     */
    private Pageable getPageable(PageRequest pageRequest) {
        return org.springframework.data.domain.PageRequest.of(pageRequest.getPage() - 1, pageRequest.getSize(), Sort.by("id"));
    }

}
