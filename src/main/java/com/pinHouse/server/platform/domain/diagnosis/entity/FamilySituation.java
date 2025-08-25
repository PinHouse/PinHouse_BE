package com.pinHouse.server.platform.domain.diagnosis.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FamilySituation {

    SUPPORTING_ELDERLY("나 또는 배우자가 노부모를 1년 이상 부양"),
    LIVING_IN_GROUP_HOME("그룹홈 거주"),
    GRANDPARENT_FAMILY("조손가족");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
