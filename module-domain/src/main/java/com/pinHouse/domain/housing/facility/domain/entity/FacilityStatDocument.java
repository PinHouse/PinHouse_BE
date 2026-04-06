package com.pinHouse.domain.housing.facility.domain.entity;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "facility_counts")
public class FacilityStatDocument {

	@Id
	private String id;

	private double radiusKm;

	private Map<FacilityType, Integer> counts = new EnumMap<>(FacilityType.class);

	private Instant updatedAt;

}
