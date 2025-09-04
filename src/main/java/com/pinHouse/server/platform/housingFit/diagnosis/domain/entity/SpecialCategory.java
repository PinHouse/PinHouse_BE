package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;


import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SpecialCategory {

    FOSTER_FAMILY_OR_CHILDCARE_TERMINATING_WITHIN_2Y("위탁가정/보육원 시설종료2년이내, 종료예정자"),
    NATIONAL_HERO_PERSON_OR_HOUSEHOLD("국가 유공자 본인/가구"),
    COMFORT_WOMAN_PERSON_OR_HOUSEHOLD("위안부 피해자 본인/가구"),
    NORTH_KOREAN_DEFECTOR("북한이탈주민 본인"),
    RETURNED_MILITARY_PERSONNEL("귀한 국군포로 본인"),
    DISABLED_PERSON_OR_HOUSEHOLD("장애인 등록자/장애인 가구"),
    TRAFFIC_ACCIDENT_SURVIVOR_CHILD_HOUSEHOLD("교통사고 유자녀 가정"),
    PUBLIC_RENTAL_EXIT("부도 공공임대 퇴거자"),
    YEONGGU_RENTAL_EXIT("영구임대 퇴거자"),
    HOUSING_VULNERABLE_OR_EMERGENCY_SUPPORT("주거 취약계층/긴급 주거지원 대상자"),
    INDUSTRIAL_WORKER("산단근로자"),
    GUARANTOR_REFUSER("보증 거절자");

    private final String description;

    @JsonValue
    public String getDescription() {
        return description;
    }
}
