package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.search.domain.entity.HouseType;
import com.pinHouse.server.platform.search.domain.entity.RentalType;
import com.pinHouse.server.platform.search.domain.entity.SupplyType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 빠른 검색을 위해 필요한 조건을 입력하면 검색됩니다.
 */
@Schema(name = "[요청][검색] 빠른 검색 요청", description = "빠른 검색 조건을 위한 요청 DTO입니다.")
public record FastSearchRequest(

        @Schema(description = "나의 기록 아이디", example = "null")
        String historyId,

        @Schema(description = "나의 핀 포인트 아이디", example = "fec9aba3-0fd9-4b75-bebf-9cb7641fd251")
        String pinPointId,

        @Schema(description = "대중교통 소요 시간(분)", example = "120")
        Integer transitTime,

        @Schema(description = "방 최소 크기 (평)", example = "5.3")
        Double minSize,

        @Schema(description = "방 최대 크기 (평)", example = "30")
        Double maxSize,

        @Schema(description = "보증금 최대값", example = "50000000")
        Integer maxDeposit,

        @Schema(description = "월 임대료 최대값", example = "300000")
        Integer maxMonthPay,

        @Schema(description = "원하는 인프라, 최대 3개까지 가능", example = "[\"문화센터\"]")
        @Size(max = 3)
        List<FacilityType> facilities,

        @Schema(description = "모집 대상", example = "[\"신혼부부\"]")
        List<RentalType> rentalTypes,

        @Schema(description = "공급 유형", example = "[\"공공임대\"]")
        List<SupplyType> supplyTypes,

        @Schema(description = "주택 유형", example = "[\"아파트\"]")
        List<HouseType> houseTypes
) {}
