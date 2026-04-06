package co.kr.pinhouse.domain.search.application.dto;

import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;

public record ComplexDistanceResponse(
		ComplexDocument complex,
		double distanceKm,
		int estimatedMinutes
) {}
