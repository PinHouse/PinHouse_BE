package com.pinHouse.server.platform.housingFit.rule.application.service;

import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;

public record Candidate(SupplyType type, boolean incomeOk) {
}
