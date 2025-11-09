package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;

import java.util.Collection;
import java.util.Map;
import java.util.List;

public interface FacilityStatDocumentRepositoryCustom {

    List<FacilityStatDocument> findByAllTypesOver(Collection<FacilityType> types, int min);

    Map<FacilityType, Integer> aggregateCounts(double lng, double lat, double radiusMeters);
}

