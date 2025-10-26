package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityStatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacilityStatDocumentRepository extends MongoRepository<FacilityStatDocument, String> {
}
