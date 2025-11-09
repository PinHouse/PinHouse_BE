package com.pinHouse.server.platform.housing.facility.domain.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;

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
