package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.facility.application.dto.NoticeFacilityListResponse;
import com.pinHouse.server.platform.housing.facility.domain.entity.Facility;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ComplexDetailResponse(
        String id,                          // 복합키 (noticeId#houseSn)
        String name,                        // 단지명
        String address,                     // 주소 정보
        String heating,                     // 난방방식
        Integer totalHouseholds,            // 총세대수
        Integer totalSupplyInNotice,        // 공급호수합계
        LocationResponse location,
        List<FacilityType> infra,           // 1KM 이내 주요 생활편의 시설
        Integer unitCount,
        List<UnitTypeResponse> unitTypes    // 평형 목록
) {

    /// 정적 팩토리 메서드
    public static ComplexDetailResponse from(ComplexDocument document, NoticeFacilityListResponse facilities) {

        /// 좌표 추출
        Location documentLocation = document.getLocation();

        return ComplexDetailResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .address(document.getAddress().getFull())
                .heating(document.getHeating())
                .totalHouseholds(
                        document.getTotalHouseholds() == null || document.getTotalHouseholds().equals("")
                                ? 0
                                : Integer.parseInt(document.getTotalHouseholds())
                )
                .totalSupplyInNotice(document.getTotalSupplyInNotice())
                .infra(facilities.infra())
                .location(LocationResponse.from(documentLocation.getLongitude(), documentLocation.getLatitude()))
                .unitCount(document.getUnitTypes().size())
                .unitTypes(UnitTypeResponse.from(document.getUnitTypes()))
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<ComplexDetailResponse> from(
            List<ComplexDocument> documents,
            Map<String, NoticeFacilityListResponse> facilityListResponseMap
    ) {
        return documents.stream()
                .map(document -> {
                    // 단지 ID로 시설 응답 찾아오기
                    NoticeFacilityListResponse facilities =
                            facilityListResponseMap.getOrDefault(document.getId(), NoticeFacilityListResponse.empty());

                    // 기존 from() 메서드 재사용
                    return ComplexDetailResponse.from(document, facilities);
                })
                .toList();
    }

}
