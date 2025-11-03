package com.pinHouse.server.platform.search.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.CommonErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 빠른 검색을 위해 필요한 조건을 입력하면 검색됩니다.
 */
@Schema(name = "[요청][검색] 빠른 검색 요청", description = "빠른 검색 조건을 위한 요청 DTO입니다.")
public record FastSearchRequest(


        @Schema(description = "나의 핀 포인트 아이디", example = "4dff2ba3-3232-4674-bddd-803ca06429ff")
        String pinPointId,

        @Schema(description = "대중교통 소요 시간(분)", example = "120")
        int transitTime,

        @Schema(description = "같이 살 인원", example = "3")
        int count,

        @Schema(description = "방 최소 크기 (평)", example = "5.3")
        double minSize,

        @Schema(description = "방 최대 크기 (평)", example = "10")
        double maxSize,

        @Schema(description = "보증금 최대값", example = "50000000")
        int maxDeposit,

        @Schema(description = "월 임대료 최대값", example = "300000")
        int maxMonthPay,

        @Schema(description = "원하는 인프라, 최대 3개까지 가능", example = "[\"도서관\"]")
        @Size(max = 3)
        List<FacilityType> facilities,

        @Schema(description = "모집 대상", example = "[\"청년\"]")
        List<RentalType> rentalTypes,

        @Schema(description = "공급 유형", example = "[\"공공임대\"]")
        List<SupplyType> supplyTypes
) {

    /// 조건
    @RequiredArgsConstructor
    public enum RentalType {

        /// 청년층
        YOUTH_SPECIAL("청년"),
        STUDENT_SPECIAL("대학생"),

        /// 가족형
        COUPLE_SPECIAL("신혼부부"),
        MULTI_CHILD_SPECIAL("다자녀"),

        /// 주거약자
        ELDER_SPECIAL("고령자"),
        DISABLED_SPECIAL("장애인"),
        SINGLE_PARENT_SPECIAL("한부모"),
        VETERAN_SPECIAL("국가유공자"),
        LOW_INCOME_SPECIAL("저소득층"),

        /// 주택보유상태
        NO_OWN_SPECIAL("무주택자"),
        OWN_SPECIAL("유주택자");

        private final String value;

        /// 한글값
        @JsonValue
        public String getValue() {
            return value;
        }


        /// 생성기
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

    /// 임대 유형
    @RequiredArgsConstructor
    @Getter
    public enum SupplyType {

        HAPPY_HOUSING("행복주택", List.of("행복주택")),
        PUBLIC_RENTAL("공공임대", List.of("국민임대", "영구임대", "통합공공임대")),
        PRIVATE_RENTAL("민간임대", List.of("매입임대", "공공지원민간임대", "5년임대", "6년임대", "10년임대", "50년임대")),
        JEONSE_RENTAL("전세형 임대", List.of("장기전세", "전세임대"));

        private final String value;
        private final List<String> includedTypes;

        @JsonValue
        public String getValue() {
            return value;
        }

        public static SupplyType fromValue(String value) {
            for (SupplyType type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            throw new CustomException(CommonErrorCode.BAD_PARAMETER);

        }
    }
}
