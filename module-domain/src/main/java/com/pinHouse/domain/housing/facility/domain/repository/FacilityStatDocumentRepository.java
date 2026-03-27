package com.pinHouse.domain.housing.facility.domain.repository;

import com.pinHouse.domain.housing.facility.domain.entity.FacilityStatDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FacilityStatDocumentRepository extends MongoRepository<FacilityStatDocument, String>, FacilityStatDocumentRepositoryCustom {
}
