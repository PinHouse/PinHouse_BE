package com.pinHouse.server.platform.search.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 빠른 검색을 위해 필요한 조건을 입력하면 검색됩니다.
 */
@Schema(name = "[요청][검색] 빠른 검색 요청", description = "빠른 검색 조건을 위한 요청 DTO입니다.")
public record FastSearchRequest(

        @Schema(description = "방 개수", example = "3")
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

        @Schema(description = "원하는 생활환경, 최대 3개까지 가능")
        @Size(max = 3)
        List<FacilityType> facilityTypes,

        @Schema(description = "공급유형 목록")
        List<SupplyType> supplyTypes,

        @Schema(description = "렌탈 유형 목록")
        List<RentalType> rentalTypes
) {

    @RequiredArgsConstructor
    enum SupplyType {

        GENERAL("일반 공급"),
        YOUTH_SPECIAL("청년 특별공급"),
        STUDENT_SPECIAL("대학생 특별공급"),
        NEWCOUPLE_SPECIAL("신혼부부 특별공급"),
        ELDER_SUPPORT_SPECIAL("고령자 부양 특별공급"),
        ELDER_SPECIAL("고령자 특별공급"),
        MULTICHILD_SPECIAL("다자녀 특별공급"),
        WEAK_SPECIAL("취약계층 특별공급"),
        BASIC_SPECIAL("기초수급자 특별공급"),
        NO_OWN_SPECIAL("무주택자 특별공급");

        private final String value;

        /**
         * enum 에서 보여주는 value 값 가져오기
         */
        @JsonValue
        public String getValue() {
            return value;
        }

        /**
         * 받은 결과를 바탕으로 enum 값 만들기
         * @param value 주는 값
         */
        @JsonCreator
        public static SupplyType fromValue(String value) {
            for (SupplyType type : SupplyType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(ErrorCode.INVALID_INPUT.getMessage());
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

        /**
         * enum 에서 보여주는 value 값 가져오기
         */
        @JsonValue
        public String getValue() {
            return value;
        }

        /**
         * 받은 결과를 바탕으로 enum 값 만들기
         * @param value 주는 값
         */
        @JsonCreator
        public static RentalType fromValue(String value) {
            for (RentalType type : RentalType.values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new IllegalArgumentException(ErrorCode.INVALID_INPUT.getMessage());
        }

    }
}
