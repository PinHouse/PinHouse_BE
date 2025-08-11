package com.pinHouse.server.platform.domain.notice;

import com.pinHouse.server.platform.domain.facility.Library;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 공지사항 및 해당 공지 주변의 도서관 리스트 정보를 담는 도메인 객체입니다.
 * 공지 객체와 주변 도서관 목록을 함께 제공합니다.
 */
@Builder
@Getter
public class NoticeInfra {

    /**
     * 공지사항 엔티티
     */
    private Notice notice;

    /**
     * 공지 주변에 위치한 도서관 리스트
     */
    private List<Library> nearbyLibraries;

    /**
     * 생성자
     *
     * @param notice 공지사항 엔티티
     * @param nearbyLibraries 주변 도서관 리스트
     */
    public static NoticeInfra of(Notice notice, List<Library> nearbyLibraries) {
        return NoticeInfra.builder()
                .notice(notice)
                .nearbyLibraries(nearbyLibraries)
                .build();

    }
}
