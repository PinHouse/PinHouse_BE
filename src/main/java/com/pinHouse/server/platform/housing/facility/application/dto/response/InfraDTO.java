package com.pinHouse.server.platform.housing.facility.application.dto.response;

import com.pinHouse.server.platform.housing.facility.domain.entity.*;
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

    /** 전시관 정보 */
    @Builder
    public record ExhibitionResponse(String name, String category, String address) {
        public static ExhibitionResponse from(Exhibition exhibition) {
            // address 조합
            String address = String.format("%s %s %s %s",
                    exhibition.getProvinceName(),   // 시·도
                    exhibition.getCityName(),   // 시·군·구
                    exhibition.getLegalDongName(),// 읍·면·동
                    exhibition.getLotNumber()      // 번지
            );

            return ExhibitionResponse.builder()
                    .name(exhibition.getPoiName())
                    .category(exhibition.getMainClassificationName())
                    .address(address)
                    .build();
        }

        public static List<ExhibitionResponse> from(List<Exhibition> exhibitions) {
            return exhibitions.stream()
                    .map(ExhibitionResponse::from)
                    .toList();
        }
    }

    /** 빨래방 정보 */
    @Builder
    public record LaundryResponse(String name, String address) {
        public static LaundryResponse from(Laundry laundry) {
            return LaundryResponse.builder()
                    .name(laundry.getBusinessName())
                    .address(laundry.getFullAddress())
                    .build();
        }
        public static List<LaundryResponse> from(List<Laundry> laundries) {
            return laundries.stream().map(LaundryResponse::from).toList();
        }
    }

    /** 병원 정보 */
    @Builder
    public record HospitalResponse(String name, String type, String address) {
        public static HospitalResponse from(Hospital hospital) {
            return HospitalResponse.builder()
                    .name(hospital.getBusinessName())
                    .type(hospital.getMedicalInstitutionType())
                    .address(hospital.getFullAddress())
                    .build();
        }
        public static List<HospitalResponse> from(List<Hospital> hospitals) {
            return hospitals.stream().map(HospitalResponse::from).toList();
        }
    }

    /** 마트 정보 */
    @Builder
    public record MartResponse(String name, String address) {
        public static MartResponse from(Mart mart) {
            return MartResponse.builder()
                    .name(mart.getBusinessName())
                    .address(mart.getFullAddress())
                    .build();
        }
        public static List<MartResponse> from(List<Mart> marts) {
            return marts.stream().map(MartResponse::from).toList();
        }
    }


    // ... (위 DTO들은 동일)

    /**
     * 공지 인프라 통합 응답 객체입니다.
     *
     * @param libraries 도서관 응답 목록
     * @param animals 동물 응답 목록
     * @param sports 스포츠 응답 목록
     * @param parks 공원 응답 목록
     * @param walkings 산책로 응답 목록
     * @param exhibitions 전시관 응답 목록
     * @param laundries 빨래방 응답 목록
     * @param hospitals 병원 응답 목록
     * @param marts 마트 응답 목록
     */
    @Builder
    public record NoticeInfraResponse(
            @Nullable List<LibraryResponse> libraries,
            @Nullable List<AnimalResponse> animals,
            @Nullable List<SportResponse> sports,
            @Nullable List<ParkResponse> parks,
            @Nullable List<WalkingResponse> walkings,
            @Nullable List<ExhibitionResponse> exhibitions,
            @Nullable List<LaundryResponse> laundries,
            @Nullable List<HospitalResponse> hospitals,
            @Nullable List<MartResponse> marts
    ) {
        /** FacilityResponse → 통합 응답 DTO로 일괄 변환 (null-safe 매핑) */
        public static NoticeInfraResponse from(FacilityResponse facilityResponse) {
            return NoticeInfraResponse.builder()
                    .libraries(facilityResponse.getLibraries() == null
                            ? null : LibraryResponse.from(facilityResponse.getLibraries()))
                    .animals(facilityResponse.getAnimals() == null
                            ? null : AnimalResponse.from(facilityResponse.getAnimals()))
                    .sports(facilityResponse.getSports() == null
                            ? null : SportResponse.from(facilityResponse.getSports()))
                    .parks(facilityResponse.getParks() == null
                            ? null : ParkResponse.from(facilityResponse.getParks()))
                    .walkings(facilityResponse.getWalkings() == null
                            ? null : WalkingResponse.from(facilityResponse.getWalkings()))
                    .exhibitions(facilityResponse.getExhibitions() == null
                            ? null : ExhibitionResponse.from(facilityResponse.getExhibitions()))
                    .laundries(facilityResponse.getLaundries() == null
                            ? null : LaundryResponse.from(facilityResponse.getLaundries()))
                    .hospitals(facilityResponse.getHospitals() == null
                            ? null : HospitalResponse.from(facilityResponse.getHospitals()))
                    .marts(facilityResponse.getMarts() == null
                            ? null : MartResponse.from(facilityResponse.getMarts()))
                    .build();
        }
    }
}
