package com.pinHouse.server.platform.application.in;

import com.pinHouse.server.platform.domain.notice.Notice;
import com.pinHouse.server.platform.domain.notice.NoticeInfra;

/**
 * - 공고 주변의 인프라 목록 조회할 인터페이스
 */
public interface NoticeInfraUseCase {

    /// 조회
    // 주변의 인프라 개수 조회
    NoticeInfra getNoticeInfraById(String noticeId);

    // 원하는 인프라 바탕으로 많이 존재하는 공고 조회
    Notice getNoticeByInfra();
}
