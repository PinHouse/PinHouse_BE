package com.pinHouse.server.platform.housing.complex.application.dto.response;

/// 결과 타입
public enum ChipType {
    WALK("#D9D9D9"),
    BUS("#C6403D"),
    SUBWAY("#164B84"),
    TRAIN("#0B3A67"),
    AIR("#2C7A7B"); // 항공용 컬러/아이콘은 디자인에 맞게 조정

    public final String defaultBg;

    ChipType(String defaultBg) {
        this.defaultBg = defaultBg;
    }
}
