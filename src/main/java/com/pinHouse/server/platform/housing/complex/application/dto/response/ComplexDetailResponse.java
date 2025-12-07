package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ComplexDetailResponse(
        String id,                          // 복합키 (noticeId#houseSn)
        String name,                        // 단지명
        String address,                     // 주소 정보
        String heating,                     // 난방방식
        Integer totalHouseholds,            // 총세대수
        Integer totalSupplyInNotice,        // 공급호수합계
        List<FacilityType> infra,           // 1KM 이내 주요 생활편의 시설
        Integer unitCount,
        List<String> unitTypes,
        String totalTime,                   // 공고 상세조회용: 대중교통 총 소요 시간 (포맷: "0시간 0분")
        DistanceResponse distance           // 임대주택 상세조회용: 전체 대중교통 정보
) {

    /// 정적 팩토리 메서드 - 임대주택 상세조회용 (DistanceResponse 전체 포함)
    public static ComplexDetailResponse from(ComplexDocument document, NoticeFacilityListResponse facilities, DistanceResponse distance) {

        return ComplexDetailResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .address(extractRegion(document.getAddress().getFull()))
                .heating(document.getHeating())
                .totalHouseholds(
                        document.getTotalHouseholds() == null || document.getTotalHouseholds().equals("")
                                ? 0
                                : Integer.parseInt(document.getTotalHouseholds())
                )
                .totalSupplyInNotice(document.getTotalSupplyInNotice())
                .infra(facilities.infra())
                .unitCount(document.getUnitTypes().size())
                .unitTypes(document.getUnitTypes().stream()
                        .map(UnitType::getTypeCode)
                        .toList())
                .totalTime(null)
                .distance(distance)
                .build();
    }

    /// 정적 팩토리 메서드 - 공고 상세조회용 (거리 정보 없음)
    public static List<ComplexDetailResponse> from(
            List<ComplexDocument> documents,
            Map<String, NoticeFacilityListResponse> facilityListResponseMap
    ) {
        return documents.stream()
                .map(document -> {
                    NoticeFacilityListResponse facilities =
                            facilityListResponseMap.getOrDefault(document.getId(), NoticeFacilityListResponse.empty());

                    return ComplexDetailResponse.builder()
                            .id(document.getId())
                            .name(document.getName())
                            .address(extractRegion(document.getAddress().getFull()))
                            .heating(document.getHeating())
                            .totalHouseholds(null)
                            .totalSupplyInNotice(null)
                            .infra(facilities.infra())
                            .unitCount(document.getUnitTypes().size())
                            .unitTypes(null)
                            .totalTime(null)
                            .distance(null)
                            .build();
                })
                .toList();
    }

    /// 정적 팩토리 메서드 - 공고 상세조회용 (totalTime 포함)
    public static List<ComplexDetailResponse> from(
            List<ComplexDocument> documents,
            Map<String, NoticeFacilityListResponse> facilityListResponseMap,
            Map<String, Integer> totalTimeMap
    ) {
        return documents.stream()
                .map(document -> {
                    NoticeFacilityListResponse facilities =
                            facilityListResponseMap.getOrDefault(document.getId(), NoticeFacilityListResponse.empty());
                    Integer totalTimeMinutes =
                            totalTimeMap.getOrDefault(document.getId(), null);

                    return ComplexDetailResponse.builder()
                            .id(document.getId())
                            .name(document.getName())
                            .address(extractRegion(document.getAddress().getFull()))
                            .heating(document.getHeating())
                            .totalHouseholds(null)
                            .totalSupplyInNotice(null)
                            .infra(facilities.infra())
                            .unitCount(document.getUnitTypes().size())
                            .unitTypes(null)
                            .totalTime(totalTimeMinutes != null ? formatTime(totalTimeMinutes) : null)
                            .distance(null)
                            .build();
                })
                .toList();
    }

    /**
     * 전체 주소에서 시/도 + 시/군/구만 추출
     * @param fullAddress 전체 주소 (예: "서울특별시 서초구 서초동 123-45")
     * @return 시/도 + 시/군/구 (예: "서울특별시 서초구")
     */
    private static String extractRegion(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }

        String[] parts = fullAddress.trim().split("\\s+");

        // 최소 2개 부분이 있어야 함 (시/도 + 시/군/구)
        if (parts.length < 2) {
            return fullAddress;
        }

        return parts[0] + " " + parts[1];
    }

    /**
     * 시간을 "0시간 0분" 형식으로 포맷팅
     * @param totalMinutes 총 시간(분)
     * @return 포맷팅된 시간 문자열 (예: "1시간 30분", "45분"), 0 이하면 null
     */
    private static String formatTime(int totalMinutes) {
        if (totalMinutes <= 0) {
            return null;
        }

        if (totalMinutes < 60) {
            return totalMinutes + "분";
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        if (minutes == 0) {
            return hours + "시간";
        }

        return hours + "시간 " + minutes + "분";
    }

}
