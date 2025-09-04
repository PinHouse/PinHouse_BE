package com.pinHouse.server.platform.facility.domain.entity;

import com.pinHouse.server.platform.notice.domain.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * <pre>
 * 공지사항 및 주변 인프라 리스트 정보를 담는 도메인 객체입니다.
 *
 * - 공지 객체와 주변 도서관, 동물, 스포츠, 산책로, 공원 목록을 함께 제공합니다.
 * - 실무 현장에서 다양한 시설 데이터 연동에 용이하게 활용합니다.
 * </pre>
 */
@Builder
@Getter
public class NoticeInfra {

    /** 공지사항 엔티티 */
    private Notice notice;

    /** 주변 도서관 리스트 */
    private List<Library> libraries;

    /** 주변 동물 관련 시설 리스트 */
    private List<Animal> animals;

    /** 주변 스포츠 시설 리스트 */
    private List<Sport> sports;

    /** 주변 산책로 리스트 */
    private List<Walking> walkings;

    /** 주변 공원 리스트 */
    private List<Park> parks;

    /**
     * NoticeInfra 팩토리 메서드
     *
     * @param notice 공지사항 엔티티
     * @param libraries 주변 도서관 리스트
     * @param animals 주변 동물 관련 시설 리스트
     * @param sports 주변 스포츠 시설 리스트
     * @param walkings 주변 산책로 리스트
     * @param parks 주변 공원 리스트
     * @return NoticeInfra 도메인 객체
     */
    public static NoticeInfra of(
            Notice notice,
            List<Library> libraries,
            List<Animal> animals,
            List<Sport> sports,
            List<Walking> walkings,
            List<Park> parks
    ) {
        return NoticeInfra.builder()
                .notice(notice)
                .libraries(libraries)
                .animals(animals)
                .sports(sports)
                .walkings(walkings)
                .parks(parks)
                .build();
    }
}
