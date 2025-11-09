package com.pinHouse.server.platform.like.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import lombok.Builder;
import java.util.List;

@Builder
public record UnityTypeLikeResponse(

        String id,              // 아이디
        String complexId,       // 주택 아이디
        String typeCode,        // 공급유형 (예: 26A)
        Integer quota,          // 모집호수 정보
        Double exclusiveAreaM2, // 전용면적
        Long deposit,           // 임대보증금
        Integer monthlyRent,    // 월임대료(원)
        boolean liked
) {

    /// 정적 팩토리 메서드
    public static UnityTypeLikeResponse from(ComplexDocument document) {

        /// 해당되는 유닛은 하나밖에 없음
        UnitType unitType = document.getUnitTypes().getFirst();

        return UnityTypeLikeResponse.builder()
                .id(unitType.getTypeId())
                .complexId(document.getComplexKey())
                .typeCode(unitType.getTypeCode())
                .quota(unitType.getQuota().getTotal())
                .exclusiveAreaM2(unitType.getExclusiveAreaM2())
                .deposit(unitType.getDeposit().getTotal())
                .monthlyRent(unitType.getMonthlyRent())
                .liked(true) // 여기는 나의 좋아요만 보는 곳이기 때문
                .build();
    }

    /// 정적 팩토리 메서드
    public static List<UnityTypeLikeResponse> from(List<ComplexDocument> complexDocuments) {
        return complexDocuments.stream()
                .map(UnityTypeLikeResponse::from)
                .toList();
    }
}
