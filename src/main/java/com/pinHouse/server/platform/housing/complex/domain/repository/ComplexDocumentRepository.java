package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ComplexDocumentRepository extends MongoRepository<ComplexDocument, String> {

    List<ComplexDocument> findByNoticeId(String noticeId);

    @Query(value = "{ 'unitTypes.typeId': { $in: ?0 } }",
            fields = "{ 'complexId': 1, 'name': 1, 'unitTypes.$': 1 }")
    List<ComplexDocument> findFirstMatchingUnitType(List<ObjectId> typeIds);

    /// 존재하는 모음
    List<ComplexDocument> findByIdIsIn(List<String> complexIds);

    @Query("{ 'location' : { $geoWithin : { $centerSphere: [ [?0, ?1], ?2 ] } } }")
    List<ComplexDocument> findByLocation(double lng, double lat, double radiusInRadians);

}
