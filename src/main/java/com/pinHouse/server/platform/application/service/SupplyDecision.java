package com.pinHouse.server.platform.application.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class SupplyDecision {
    private final boolean eligible;
    private final String supplyTypeCode;
    private final String displayName;
    private final int score;
    private final Map<String, Object> meta;
}
