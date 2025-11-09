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
        List<String> unitTypes
) {

    /// 정적 팩토리 메서드
    public static ComplexDetailResponse from(ComplexDocument document, NoticeFacilityListResponse facilities) {

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
                .unitCount(document.getUnitTypes().size())
                .unitTypes(document.getUnitTypes().stream()
                        .map(UnitType::getTypeCode)
                        .toList())
                .build();
    }

    /// 정적 팩토리 메서드
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
                            .address(document.getAddress().getFull())
                            .heating(document.getHeating())
                            .totalHouseholds(null)
                            .totalSupplyInNotice(null)
                            .infra(facilities.infra())
                            .unitCount(document.getUnitTypes().size())
                            .unitTypes(null)
                            .build();
                })
                .toList();
    }

}
