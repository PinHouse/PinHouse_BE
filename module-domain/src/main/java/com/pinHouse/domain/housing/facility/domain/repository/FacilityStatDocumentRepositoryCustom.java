package com.pinHouse.domain.housing.facility.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pinHouse.domain.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.domain.housing.facility.domain.entity.FacilityType;

public interface FacilityStatDocumentRepositoryCustom {

	List<FacilityStatDocument> findByAllTypesOver(Collection<FacilityType> types, int min);

	Map<FacilityType, Integer> aggregateCounts(double lng, double lat, double radiusMeters);
}
