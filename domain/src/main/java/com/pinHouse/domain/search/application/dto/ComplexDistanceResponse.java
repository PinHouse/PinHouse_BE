package com.pinHouse.domain.search.application.dto;

import com.pinHouse.domain.housing.complex.domain.entity.ComplexDocument;

public record ComplexDistanceResponse(
        ComplexDocument complex,
        double distanceKm,
        int estimatedMinutes
) {}
