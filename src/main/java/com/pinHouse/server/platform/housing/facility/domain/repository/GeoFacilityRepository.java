package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.Facility;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;

import java.util.List;
public interface GeoFacilityRepository<T extends Facility> {

    List<T> findByLocation(double lng, double lat, double radiusInRadians);

    long countByLocation(double lng, double lat, double radiusInRadians);

    FacilityType supportsType();
}
