package com.pinHouse.server.platform.housing.facility.domain.repository.infra;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Mart;
import com.pinHouse.server.platform.housing.facility.domain.repository.GeoFacilityRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MartDocumentRepository extends MongoRepository<Mart, String>, GeoFacilityRepository<Mart> {

    @Query(value = "{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
    List<Mart> findByLocation(double longitude, double latitude, double radiusInRadians);

    default FacilityType supportsType() { return FacilityType.STORE; }

}
