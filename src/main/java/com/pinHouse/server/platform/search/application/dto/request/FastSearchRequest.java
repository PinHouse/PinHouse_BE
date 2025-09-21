package com.pinHouse.server.platform.search.application.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.facility.application.dto.request.FacilityType;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 빠른 검색을 위해 필요한 조건을 입력하면 검색됩니다.
 */
@Getter
public class FastSearchRequest {

    /// 방 개수
    @Parameter(example = "3")
    private int count;

    /// 방 크기
    @Parameter(example = "30.00")
    private float minSize;

    @Parameter(example = "84.6")
    private float maxSize;

    /// 보증금 범위
    @Parameter(example = "100000")
    private int minPrice;

    @Parameter(example = "10000000")
    private int maxPrice;

    /// 나의 핀 포인트 아이디
    @Parameter(example = "1")
    private long pinPointId;

    /// 대중교통 시간
    @Parameter(example = "120")
    private int transitTime;

    /// 원하는 생활환경, 최대 3개까지 가능
    @Size(max = 3)
    private List<FacilityType> facilityTypes;

    /// 공급유형
    @Parameter(example = "")
    private List<SupplyType> supplyTypes;

    /// 렌탈
    @Parameter(example = "")
    private List<RentalType> rentalTypes;


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
