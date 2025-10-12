package com.pinHouse.server.platform.housing.facility.domain.repository.infra;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Laundry;
import com.pinHouse.server.platform.housing.facility.domain.repository.GeoFacilityRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LaundryDocumentRepository extends MongoRepository<Laundry, String>, GeoFacilityRepository<Laundry> {

    @Query(value = "{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
    List<Laundry> findByLocation(double longitude, double latitude, double radiusInRadians);

    default FacilityType supportsType() { return FacilityType.LAUNDRY; }

}
