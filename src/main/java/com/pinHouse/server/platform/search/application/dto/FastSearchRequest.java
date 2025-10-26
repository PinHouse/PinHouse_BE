package com.pinHouse.server.platform.search.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 빠른 검색을 위해 필요한 조건을 입력하면 검색됩니다.
 */
@Schema(name = "[요청][검색] 빠른 검색 요청", description = "빠른 검색 조건을 위한 요청 DTO입니다.")
public record FastSearchRequest(

        @Schema(description = "같이 살 인원", example = "3")
        int count,

        @Schema(description = "방 최소 크기 (제곱미터)", example = "30.00")
        float minSize,

        @Schema(description = "방 최대 크기 (제곱미터)", example = "84.6")
        float maxSize,

        @Schema(description = "보증금 최소값", example = "100000")
        int minPrice,

        @Schema(description = "보증금 최대값", example = "10000000")
        int maxPrice,

        @Schema(description = "나의 핀 포인트 아이디", example = "1")
        long pinPointId,

        @Schema(description = "대중교통 소요 시간(분)", example = "120")
        int transitTime,

        @Schema(description = "원하는 인프라, 최대 3개까지 가능")
        @Size(max = 3)
        List<FacilityType> facilityTypes,

        @Schema(description = "모집 대상")
        List<SupplyType> supplyTypes,

        @Schema(description = "공급 유형")
        List<RentalType> rentalTypes
) {

    @RequiredArgsConstructor
    enum SupplyType {

        GENERAL("일반"),
        YOUTH_SPECIAL("청년"),
        STUDENT_SPECIAL("대학생"),
        COUPLE_SPECIAL("신혼부부"),
        ELDER_SPECIAL("고령자"),
        MULTI_CHILD_SPECIAL("다자녀"),
        BASIC_SPECIAL("기초수급자"),
        WEAK_SPECIAL("취약계층"),
        NO_OWN_SPECIAL("무주택자");

        private final String value;

        /// 한글값
        @JsonValue
        public String getValue() {
            return value;
        }


        /// 생성기
        @JsonCreator
        public static SupplyType fromValue(String value) {
            for (SupplyType type : SupplyType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new CustomException(CommonErrorCode.BAD_PARAMETER);
        }
    }

    @RequiredArgsConstructor
    enum RentalType {

        PUBLIC_INTEGRATED("통합공공임대"),
        PUBLIC_RENTAL("공공임대"),
        NATIONAL_RENTAL("국민임대"),
        HAPPY_HOUSING("행복주택"),
        PRIVATE_RENTAL("민간임대");

        private final String value;

        /// 한글값
        @JsonValue
        public String getValue() {
            return value;
        }


        /// 생성값
        @JsonCreator
        public static RentalType fromValue(String value) {
            for (RentalType type : RentalType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new CustomException(CommonErrorCode.BAD_PARAMETER);
        }

    }
}
