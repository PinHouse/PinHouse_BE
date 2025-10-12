package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ComplexDocumentRepository extends MongoRepository<ComplexDocument, String> {

    List<ComplexDocument> findByNoticeId(String noticeId);

    Optional<ComplexDocument> findByComplexKey(String complexKey);
}
