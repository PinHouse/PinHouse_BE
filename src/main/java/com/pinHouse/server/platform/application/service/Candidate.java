package com.pinHouse.server.platform.application.service;

import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;

public record Candidate(SupplyType type, boolean incomeOk) {
}
