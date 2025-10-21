package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComplexDocumentRepository extends MongoRepository<ComplexDocument, String> {

    List<ComplexDocument> findByNoticeId(String noticeId);

    Optional<ComplexDocument> findByComplexKey(String complexKey);

    @Query(value = "{ 'unitTypes.typeId': { $in: ?0 } }",
            fields = "{ 'complexKey': 1, 'name': 1, 'unitTypes.$': 1 }")
    List<ComplexDocument> findFirstMatchingUnitType(List<ObjectId> typeIds);
}
