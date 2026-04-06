package com.pinHouse.domain.user.application.dto;

import java.util.List;

import com.pinHouse.domain.housing.facility.domain.entity.FacilityType;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "[요청][사용자] 사용자 요청", description = "사용자 요청을 위한 DTO입니다.")
public record UserRequest(
		@Schema(description = "사용자가 선택한 시설 유형 목록", example = "[\"문화센터\", \"산책길\"]")
		List<FacilityType> facilityTypes
) {

}
