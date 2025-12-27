package com.pinHouse.server.platform.housing.complex.application.dto.response;

import com.pinHouse.server.platform.housing.complex.domain.entity.Quota;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(name = "[응답][유닛타입] 유닛타입 정보", description = "유닛타입(주택형)의 상세 정보를 응답할 때 사용되는 DTO")
public record UnitTypeResponse(
        @Schema(description = "유닛타입 ID (고유 식별자)", example = "a1b2c3d4e5f6g7h8i9j0k1l2")
        String typeId,

        @Schema(description = "타입 코드 (주택형)", example = "59A")
        String typeCode,

        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg", nullable = true)
        String thumbnail,

        @Schema(description = "모집 호수 (총 공급 세대수)", example = "50")
        Integer quota,

        @Schema(description = "전용 면적 (제곱미터)", example = "59.42")
        Double exclusiveAreaM2,

        @Schema(description = "보증금 정보 (최소/보통/최대)")
        DepositResponse deposit,

        @Schema(description = "좋아요 여부", example = "true")
        boolean liked,

        @Schema(description = "공급 그룹 정보 (대상 계층)", example = "[\"신혼부부\", \"청년\"]")
        List<String> group
) {

    /// 정적 팩토리 메서드
    public static UnitTypeResponse from(UnitType unitType, DepositResponse deposit, boolean liked) {

        Quota typeQuota = unitType.getQuota();

        return UnitTypeResponse.builder()
                .typeId(unitType.getTypeId())
                .typeCode(unitType.getTypeCode())
                .thumbnail(null)
                .exclusiveAreaM2(unitType.getExclusiveAreaM2())
                .deposit(deposit)
                .quota(typeQuota.getTotal())
                .liked(liked)
                .group(unitType.getGroup())
                .build();

    }

    /// 정적 팩토리 메서드 (오버로드 - 좋아요 정보 없을 때)
    public static UnitTypeResponse from(UnitType unitType, DepositResponse deposit) {
        return from(unitType, deposit, false);
    }
}
