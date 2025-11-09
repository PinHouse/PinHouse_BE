package com.pinHouse.server.platform.search.application.dto;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;

public record ComplexDistanceResponse(
        ComplexDocument complex,
        double distanceKm,
        int estimatedMinutes
) {}
