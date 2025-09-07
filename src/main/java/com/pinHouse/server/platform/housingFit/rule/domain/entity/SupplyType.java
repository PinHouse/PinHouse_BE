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
    SPECIAL("특별공급");

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
}
