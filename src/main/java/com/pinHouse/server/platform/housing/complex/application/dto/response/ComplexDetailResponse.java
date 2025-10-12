package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.Location;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import lombok.Builder;

import java.util.List;

@Builder
public record ComplexDetailResponse(
        String id,                          // 복합키 (noticeId#houseSn)
        String name,                        // 단지명
        String address,                     // 주소 정보
        String heating,                     // 난방방식
        Integer totalHouseholds,            // 총세대수
        Integer totalSupplyInNotice,        // 공급호수합계
        LocationResponse location,

        List<UnitTypeResponse> unitTypes    // 평형 목록
) {

    /// 정적 팩토리 메서드
    public static ComplexDetailResponse from(ComplexDocument document) {

        /// 좌표 추출
        Location documentLocation = document.getLocation();

        return ComplexDetailResponse.builder()
                .id(document.getComplexKey())
                .name(document.getName())
                .address(document.getAddress().getFull())
                .heating(document.getHeating())
                .totalHouseholds(
                        document.getTotalHouseholds() == null || document.getTotalHouseholds().equals("")
                                ? 0
                                : Integer.parseInt(document.getTotalHouseholds())
                )
                .totalSupplyInNotice(document.getTotalSupplyInNotice())
                .location(LocationResponse.from(documentLocation.getLongitude(), documentLocation.getLatitude()))
                .unitTypes(UnitTypeResponse.from(document.getUnitTypes()))
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<ComplexDetailResponse> from(List<ComplexDocument> documents) {
        return documents.stream()
                .map(ComplexDetailResponse::from)
                .toList();
    }
}
