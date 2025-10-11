package com.pinHouse.server.platform.user.application.dto;

import com.pinHouse.server.platform.housing.facility.domain.entity.infra.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "[요청][사용자] 사용자 요청", description = "사용자 요청을 위한 DTO입니다.")
public record UserRequest(
        @Schema(description = "사용자가 선택한 시설 유형 목록", example = "[\"도서관\", \"산책로\"]")
        List<FacilityType> facilityTypes
) {

}
