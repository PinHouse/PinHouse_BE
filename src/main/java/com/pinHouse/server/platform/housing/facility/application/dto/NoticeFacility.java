package com.pinHouse.server.platform.housing.facility.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 주변 인프라 리스트 정보를 담는 객체입니다.
 * - 공지 객체와 주변 도서관, 동물, 스포츠, 산책로, 공원 목록을 함께 제공합니다.
 * - 실무 현장에서 다양한 시설 데이터 연동에 용이하게 활용합니다.
 * </pre>
 */
@Builder
@Getter
public class NoticeFacility {

    /** 공지사항 엔티티 */
    private ComplexDocument notice;

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

    /** 주변 전시관 리스트 */
    private List<Exhibition> exhibitions;

    /** 주변 빨래방 리스트 */
    private List<Laundry> laundries;

    /** 주변 병원 리스트 */
    private List<Hospital> hospitals;

    /** 주변 마트 리스트 */
    private List<Mart> marts;



    /// 정적 팩토리 메서드
    public static NoticeFacility of(
            ComplexDocument notice,
            List<Library> libraries,
            List<Animal> animals,
            List<Sport> sports,
            List<Walking> walkings,
            List<Park> parks,
            List<Exhibition> exhibitions,
            List<Laundry> laundries,
            List<Hospital> hospitals,
            List<Mart> marts
    ) {
        return NoticeFacility.builder()
                .notice(notice)
                .libraries(libraries)
                .animals(animals)
                .sports(sports)
                .walkings(walkings)
                .parks(parks)
                .exhibitions(exhibitions)
                .laundries(laundries)
                .hospitals(hospitals)
                .marts(marts)
                .build();
    }
}
