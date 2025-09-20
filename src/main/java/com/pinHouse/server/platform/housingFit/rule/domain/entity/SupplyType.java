package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;
import java.util.*;

@RequiredArgsConstructor
public enum SupplyType {

    GENERAL("일반 공급"),
    YOUTH_SPECIAL("청년 특별공급"),
    STUDENT_SPECIAL("대학생 특별공급"),
    NEWCOUPLE_SPECIAL("신혼부부 특별공급"),
    SINGLE_PARENT_SPECIAL("한부모 특별공급"),
    ELDER_SPECIAL("고령자 특별공급"),
    MULTICHILD_SPECIAL("다자녀 특별공급"),
    MINOR_SPECIAL("신생아 특별공급"),
    FIRST_SPECIAL("생애최초 특별공급"),
    ELDER_SUPPORT_SPECIAL("고령자 부양 특별공급"),
    SPECIAL("미성년자 특별계층 공급"),


    /// 특별계층 항목들
    DEMOLITION("철거민 특별공급"),
    NATIONAL_MERIT("국가유공자 특별공급"),
    LONG_SERVICE_VETERAN("장기복무 제대군인 특별공급"),
    NORTH_DEFECTOR("북한이탈주민 특별공급"),
    COMFORT_WOMAN_VICTIM("위안부 피해자 특별공급"),
    DISABLED("장애인 특별공급"),
    NON_HOUSING_RESIDENT("비주택거주자 특별공급"),
    PERMANENT_LEASE_EVICTEE("영구임대 퇴거자 특별공급"),
    TEMPORARY_STRUCTURE_RESIDENT("비닐 간이공작물 거주자 특별공급"),
    UNAUTHORIZED_BUILDING_TENANT("무허가 건축물 세입자 특별공급");

    private final String value;

    @JsonGetter
    public String getValue() {
        return value;
    }


    /**
     * 모든 SupplyType enum 반환
     */
    public static List<SupplyType> getAllTypes() {
        return Arrays.asList(SupplyType.values());
    }

    /**
     * 모든 특별계층 SupplyType enum 반환
     */
    public static List<SupplyType> getSpecialTypes() {
        return Arrays.asList(DEMOLITION, NATIONAL_MERIT, LONG_SERVICE_VETERAN,
                NORTH_DEFECTOR, COMFORT_WOMAN_VICTIM, DISABLED,
                NON_HOUSING_RESIDENT, PERMANENT_LEASE_EVICTEE, TEMPORARY_STRUCTURE_RESIDENT,
                UNAUTHORIZED_BUILDING_TENANT);
    }
}
