package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.application.in.NoticeInfraUseCase;
import com.pinHouse.server.platform.application.out.facility.LibraryPort;
import com.pinHouse.server.platform.application.out.notice.NoticePort;
import com.pinHouse.server.platform.domain.facility.Library;
import com.pinHouse.server.platform.domain.notice.Notice;
import com.pinHouse.server.platform.domain.notice.NoticeInfra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeInfraService implements NoticeInfraUseCase {

    /// 의존성 주입
    private final NoticePort noticePort;
    private final LibraryPort libraryPort;

    /// 상수
    private final double radiusKm = 1.5;
    private final double radiusInRadians = radiusKm / 6371.0;

    // =================
    //  주변 인프라 조회
    // =================

    /// 주변의 인프라 개수 조회
    @Override
    public NoticeInfra getNoticeInfraById(String noticeId) {

        /// 예외 처리
        Notice notice = getNotice(noticeId);

        /// 주변에 존재하는 도서관 가져오기
        List<Library> libraries = libraryPort.loadLibrariesNearBy(
                notice.getLocation().getLongitude(),
                notice.getLocation().getLatitude(),
                radiusInRadians);

        /// 객체 생성
        return NoticeInfra.of(notice, libraries);
    }

    ///
    @Override
    public Notice getNoticeByInfra() {
        return null;
    }

    // =================
    //  인프라 바탕으로 공고 조회
    // =================




    // =================
    //  내부 함수
    // =================

    private Notice getNotice(String noticeId) {
        return noticePort.loadById(noticeId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.NOT_NOTICE.getMessage()));
    }

}
