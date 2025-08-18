package com.pinHouse.server.platform.domain.diagnosis.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SupplyType {

    GENERAL("일반 공급"),
    YOUTH_SPECIAL("청년 특별공급"),
    NEWCOUPLE_SPECIAL("신혼부부 특별공급"),
    ELDER_SPECIAL("고령자 특별공급"),
    MULTICHILD_SPECIAL("다자녀 특별공급"),
    ELDER_SUPPORT_SPECIAL("고령자 부양 특별공급"),
    SPECIAL("특수 대상자 특별공급");

    private final String value;

    @JsonGetter
    public String getValue(){
        return value;
    }

}
