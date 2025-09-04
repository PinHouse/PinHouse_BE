package com.pinHouse.server.platform.notice.application.dto.response;

import com.pinHouse.server.platform.facility.domain.*;
import com.pinHouse.server.platform.notice.domain.NoticeInfra;
import io.micrometer.common.lang.Nullable;
import lombok.Builder;
import java.util.List;

/**
 * 인프라 관련 응답 DTO 클래스입니다.
 */
public record InfraDTO() {

    /**
     * 도서관 정보 응답 객체입니다.
     *
     * @param libraryName    도서관 이름
     * @param address 도서관 주소
     */

    @Builder
    public record LibraryResponse(
            String libraryName,
            String address
    ) {

        /// 정적 팩토리 메서드
        public static LibraryResponse from(Library library) {
            return LibraryResponse.builder()
                    .libraryName(library.getName())
                    .address(library.getAddress())
                    .build();
        }

        /// 정적 팩토리 메서드
        public static List<LibraryResponse> from(List<Library> libraries) {
            return libraries.stream()
                    .map(LibraryResponse::from)
                    .toList();
        }
    }

    /**
     * 동물 관련 시설 응답 객체입니다.
     *
     * @param name 시설명
     * @param category 카테고리명
     */
    @Builder
    public record AnimalResponse(
            String name,
            String category,
            String address
    ) {
        /** 도메인 Animal → 응답 객체 변환 */
        public static AnimalResponse from(Animal animal) {
            return AnimalResponse.builder()
                    .name(animal.getName())
                    .category(animal.getCategory())
                    .address(animal.getAddress())
                    .build();
        }

        /** 도메인 List<Animal> → 응답 목록 변환 */
        public static List<AnimalResponse> from(List<Animal> animals) {
            return animals.stream()
                    .map(AnimalResponse::from)
                    .toList();
        }
    }

    /**
     * 스포츠 시설 응답 객체입니다.
     *
     * @param name 시설명
     * @param type 시설 유형명
     */
    @Builder
    public record SportResponse(
            String name,
            String type,
            String address
    ) {
        /** 도메인 Sport → 응답 객체 변환 */
        public static SportResponse from(Sport sport) {
            return SportResponse.builder()
                    .name(sport.getName())
                    .type(sport.getFacilityTypeName())
                    .address(sport.getAddress())
                    .build();
        }

        /** 도메인 List<Sport> → 응답 목록 변환 */
        public static List<SportResponse> from(List<Sport> sports) {
            return sports.stream()
                    .map(SportResponse::from)
                    .toList();
        }
    }

    /**
     * 공원 시설 응답 객체입니다.
     *
     * @param name 공원 명칭
     * @param category 분류명
     */
    @Builder
    public record ParkResponse(
            String name,
            String category
    ) {
        /** 도메인 Park → 응답 객체 변환 */
        public static ParkResponse from(Park park) {
            return ParkResponse.builder()
                    .name(park.getName())
                    .category(park.getCategory())
                    .build();
        }

        /** 도메인 List<Park> → 응답 목록 변환 */
        public static List<ParkResponse> from(List<Park> parks) {
            return parks.stream()
                    .map(ParkResponse::from)
                    .toList();
        }
    }

    /**
     * 산책로 정보 응답 객체입니다.
     *
     * @param name 코스명
     * @param level 난이도
     */
    @Builder
    public record WalkingResponse(
            String name,
            String level,
            String address
    ) {
        /** 도메인 Walking → 응답 객체 변환 */
        public static WalkingResponse from(Walking walking) {
            return WalkingResponse.builder()
                    .name(walking.getWalkingCourseName())
                    .level(walking.getCourseLevelName())
                    .address(walking.getAddress())
                    .build();
        }

        /** 도메인 List<Walking> → 응답 목록 변환 */
        public static List<WalkingResponse> from(List<Walking> walkings) {
            return walkings.stream()
                    .map(WalkingResponse::from)
                    .toList();
        }
    }

    /**
     * 공지 인프라 통합 응답 객체입니다.
     *
     * @param libraries 도서관 응답 목록
     * @param animals 동물 응답 목록
     * @param sports 스포츠 응답 목록
     * @param parks 공원 응답 목록
     * @param walkings 산책로 응답 목록
     */
    @Builder
    public record NoticeInfraResponse(
            @Nullable List<LibraryResponse> libraries,
            @Nullable List<AnimalResponse> animals,
            @Nullable List<SportResponse> sports,
            @Nullable List<ParkResponse> parks,
            @Nullable List<WalkingResponse> walkings
    ) {
        /** NoticeInfra → 응답 DTO로 일괄 변환 */
        public static NoticeInfraResponse from(NoticeInfra noticeInfra) {
            return NoticeInfraResponse.builder()
                    .libraries(LibraryResponse.from(noticeInfra.getLibraries()))
                    .animals(AnimalResponse.from(noticeInfra.getAnimals()))
                    .sports(SportResponse.from(noticeInfra.getSports()))
                    .parks(ParkResponse.from(noticeInfra.getParks()))
                    .walkings(WalkingResponse.from(noticeInfra.getWalkings()))
                    .build();
        }
    }
}
