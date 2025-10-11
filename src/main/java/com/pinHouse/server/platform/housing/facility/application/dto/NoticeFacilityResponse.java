package com.pinHouse.server.platform.housing.facility.application.dto;

import com.pinHouse.server.platform.housing.facility.domain.entity.*;
import io.micrometer.common.lang.Nullable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import java.util.List;

/**
 * 인프라 관련 응답 DTO 클래스입니다.
 */
@Schema(name = "[응답][인프라] 공고 인프라 통합 응답", description = "공고 인프라 관련 시설 목록 응답 DTO입니다.")
@Builder
public record NoticeFacilityResponse(
        @Schema(description = "도서관 응답 목록")
        @Nullable List<LibraryResponse> libraries,

        @Schema(description = "동물 관련 시설 응답 목록")
        @Nullable List<AnimalResponse> animals,

        @Schema(description = "스포츠 시설 응답 목록")
        @Nullable List<SportResponse> sports,

        @Schema(description = "공원 응답 목록")
        @Nullable List<ParkResponse> parks,

        @Schema(description = "산책로 응답 목록")
        @Nullable List<WalkingResponse> walkings,

        @Schema(description = "전시관 응답 목록")
        @Nullable List<ExhibitionResponse> exhibitions,

        @Schema(description = "빨래방 응답 목록")
        @Nullable List<LaundryResponse> laundries,

        @Schema(description = "병원 응답 목록")
        @Nullable List<HospitalResponse> hospitals,

        @Schema(description = "마트 응답 목록")
        @Nullable List<MartResponse> marts
) {
    /** FacilityResponse → 통합 응답 DTO로 일괄 변환 (null-safe 매핑) */
    public static NoticeFacilityResponse from(NoticeFacility noticeFacility) {
        return NoticeFacilityResponse.builder()
                .libraries(noticeFacility.getLibraries() == null
                        ? null : LibraryResponse.from(noticeFacility.getLibraries()))
                .animals(noticeFacility.getAnimals() == null
                        ? null : AnimalResponse.from(noticeFacility.getAnimals()))
                .sports(noticeFacility.getSports() == null
                        ? null : SportResponse.from(noticeFacility.getSports()))
                .parks(noticeFacility.getParks() == null
                        ? null : ParkResponse.from(noticeFacility.getParks()))
                .walkings(noticeFacility.getWalkings() == null
                        ? null : WalkingResponse.from(noticeFacility.getWalkings()))
                .exhibitions(noticeFacility.getExhibitions() == null
                        ? null : ExhibitionResponse.from(noticeFacility.getExhibitions()))
                .laundries(noticeFacility.getLaundries() == null
                        ? null : LaundryResponse.from(noticeFacility.getLaundries()))
                .hospitals(noticeFacility.getHospitals() == null
                        ? null : HospitalResponse.from(noticeFacility.getHospitals()))
                .marts(noticeFacility.getMarts() == null
                        ? null : MartResponse.from(noticeFacility.getMarts()))
                .build();
    }
}

    @Schema(name = "[응답][도서관] 도서관 정보 응답", description = "도서관 이름과 주소 정보를 담고 있습니다.")
    @Builder
    record LibraryResponse(
            @Schema(description = "도서관 이름", example = "강남 도서관")
            String libraryName,

            @Schema(description = "도서관 주소", example = "서울특별시 강남구 테헤란로 123")
            String address)
{
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

    @Schema(name = "[응답][동물] 동물 관련 시설 응답", description = "동물 관련 시설의 이름, 카테고리 및 주소 정보를 담고 있습니다.")
    @Builder
    record AnimalResponse(
            @Schema(description = "시설명", example = "서울 동물원")
            String name,

            @Schema(description = "카테고리명", example = "동물원")
            String category,

            @Schema(description = "시설 주소", example = "서울특별시 중구 세종대로 110")
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

    @Schema(name = "[응답][스포츠] 스포츠 시설 응답", description = "스포츠 시설의 이름, 유형 및 주소 정보를 담고 있습니다.")
    @Builder
    record SportResponse(
            @Schema(description = "시설명", example = "올림픽 체육관")
            String name,

            @Schema(description = "시설 유형명", example = "체육관")
            String type,

            @Schema(description = "시설 주소", example = "서울특별시 송파구 올림픽로 25")
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

    @Schema(name = "[응답][공원] 공원 정보 응답", description = "공원의 명칭과 분류 정보를 담고 있습니다.")
    @Builder
    record ParkResponse(
            @Schema(description = "공원 명칭", example = "서울숲")
            String name,

            @Schema(description = "분류명", example = "도심 공원")
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

    @Schema(name = "[응답][산책로] 산책로 정보 응답", description = "산책로의 코스명, 난이도, 주소 정보를 담고 있습니다.")
    @Builder
    record WalkingResponse(
            @Schema(description = "코스명", example = "한강 자전거 산책로")
            String name,

            @Schema(description = "난이도", example = "중급")
            String level,

            @Schema(description = "주소", example = "서울특별시 영등포구 여의도동")
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

    @Schema(name = "[응답][전시관] 전시관 정보 응답", description = "전시관의 이름, 분류 및 주소 정보를 담고 있습니다.")
    @Builder
    record ExhibitionResponse(
            @Schema(description = "전시관명", example = "국립중앙박물관")
            String name,

            @Schema(description = "분류명", example = "역사 박물관")
            String category,

            @Schema(description = "주소", example = "서울특별시 용산구 서빙고로 137")
            String address)
    {
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
    @Schema(name = "[응답][빨래방] 빨래방 정보 응답", description = "빨래방의 이름과 주소 정보를 담고 있습니다.")
    @Builder
    record LaundryResponse(
            @Schema(description = "빨래방 이름", example = "해피워시 빨래방")
            String name,

            @Schema(description = "주소", example = "서울특별시 강서구 화곡동 123-4")
            String address)
    {
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

    @Schema(name = "[응답][병원] 병원 정보 응답", description = "병원의 이름, 유형 및 주소 정보를 담고 있습니다.")
    @Builder
    record HospitalResponse(
            @Schema(description = "병원 이름", example = "삼성서울병원")
            String name,

            @Schema(description = "병원 유형", example = "종합병원")
            String type,

            @Schema(description = "주소", example = "서울특별시 강남구 일원동 50")
            String address)
{

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

    @Schema(name = "[응답][마트] 마트 정보 응답", description = "마트의 이름과 주소 정보를 담고 있습니다.")
    @Builder
    record MartResponse(
            @Schema(description = "마트 이름", example = "이마트 강남점")
            String name,

            @Schema(description = "주소", example = "서울특별시 강남구 논현동 123-45")
            String address
    ) {

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
