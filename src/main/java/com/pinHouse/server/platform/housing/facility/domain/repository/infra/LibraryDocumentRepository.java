package com.pinHouse.server.platform.housing.facility.domain.repository.infra;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.housing.facility.domain.entity.infra.Library;
import com.pinHouse.server.platform.housing.facility.domain.repository.GeoFacilityRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LibraryDocumentRepository extends MongoRepository<Library, String>, GeoFacilityRepository<Library> {

    // 좌표 주변의 특정 반경이상 주소 검색
    @Query(value = "{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
    List<Library> findByLocation(double longitude, double latitude, double radiusInRadians);

    default FacilityType supportsType() { return FacilityType.LIBRARY; }

}
