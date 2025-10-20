package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ComplexDocumentRepository extends MongoRepository<ComplexDocument, String> {

    List<ComplexDocument> findByNoticeId(String noticeId);

    Optional<ComplexDocument> findByComplexKey(String complexKey);

    @Query(value = "{ '_id': ?0, 'unitTypes.typeCode': ?1 }",
            fields = "{ 'unitTypes.$': 1 }")
    List<ComplexDocument> findByTypeCode(String typeCode);
}
