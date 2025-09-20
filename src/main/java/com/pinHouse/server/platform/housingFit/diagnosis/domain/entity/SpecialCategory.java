package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;


import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SpecialCategory {

    /// 1) 소득 관련 특수 계층
    NO("주거급여 수급자"),
    LIFE("생계/의료급여 수급자"),

    /// 13) 가정 특수 계층
    SINGLE_PARENT("한부모 가정"),
    PROTECTED_SINGLE_PARENT("보호대상 한부모 가정"),
    RELATIVE_FOSTER("친인척 위탁가정"),
    SUBSTITUTE_CARE("대리양육가정"),

    /// 15) 기타 특수 계층
    DEMOLITION("철거민"),
    NATIONAL_HERO_PERSON_OR_HOUSEHOLD("국가 유공자 본인/가구"),
    COMFORT_WOMAN_PERSON_OR_HOUSEHOLD("위안부 피해자 본인/가구"),
    NORTH_KOREAN_DEFECTOR("북한이탈주민 본인"),
    DISABLED_PERSON_OR_HOUSEHOLD("장애인 등록자/장애인 가구"),
    YEONGGU_RENTAL_EXIT("영구임대 퇴거자"),
    LONG_SERVICE_VETERAN("장기복무 제대군인"),
    HOUSING_VULNERABLE_OR_EMERGENCY_SUPPORT("주거 취약계층/긴급 주거지원 대상자"),


    FOSTER_FAMILY_OR_CHILDCARE_TERMINATING_WITHIN_2Y("위탁가정/보육원 시설종료2년이내, 종료예정자"),
    RETURNED_MILITARY_PERSONNEL("귀한 국군포로 본인"),
    TRAFFIC_ACCIDENT_SURVIVOR_CHILD_HOUSEHOLD("교통사고 유자녀 가정"),
    INDUSTRIAL_WORKER("산단근로자"),
    GUARANTOR_REFUSER("보증 거절자"),

    /// 17) 노인 지원
    SUPPORTING_ELDERLY("나 또는 배우자가 노부모를 1년 이상 부양"),
    LIVING_IN_GROUP_HOME("그룹홈 거주"),
    GRANDPARENT_FAMILY("조손가족");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }

    /// 변환하기
    public SupplyType toSupplyType() {
        return switch (this) {
            /// 철거민
            case DEMOLITION -> SupplyType.DEMOLITION;

            /// 국가유공자
            case NATIONAL_HERO_PERSON_OR_HOUSEHOLD -> SupplyType.NATIONAL_MERIT;

            /// 장기복무
            case LONG_SERVICE_VETERAN -> SupplyType.LONG_SERVICE_VETERAN;

            /// 북한 이탈주민
            case NORTH_KOREAN_DEFECTOR -> SupplyType.NORTH_DEFECTOR;

            /// 위안부
            case COMFORT_WOMAN_PERSON_OR_HOUSEHOLD -> SupplyType.COMFORT_WOMAN_VICTIM;

            /// 장애인
            case DISABLED_PERSON_OR_HOUSEHOLD -> SupplyType.DISABLED;

            /// 비주택자
            case HOUSING_VULNERABLE_OR_EMERGENCY_SUPPORT -> SupplyType.NON_HOUSING_RESIDENT;

            /// 영구임대 퇴거
            case YEONGGU_RENTAL_EXIT -> SupplyType.PERMANENT_LEASE_EVICTEE;

            default -> null;
        };
    }
}
