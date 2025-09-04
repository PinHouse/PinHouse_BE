package com.pinHouse.server.platform.facility.domain.repository;

import com.pinHouse.server.platform.facility.domain.entity.AnimalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AnimalDocumentRepository extends MongoRepository<AnimalDocument, String> {

    // 좌표 주변의 특정 반경이상 주소 검색
    @Query(value = "{ 'location': { $geoWithin: { $centerSphere: [ [ ?0, ?1 ], ?2 ] } } }")
    List<AnimalDocument> findByLocation(double longitude, double latitude, double radiusInRadians);

}
