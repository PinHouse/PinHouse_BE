package com.pinHouse.domain.housing.facility.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pinHouse.domain.housing.facility.domain.entity.FacilityStatDocument;

public interface FacilityStatDocumentRepository extends MongoRepository<FacilityStatDocument, String>, FacilityStatDocumentRepositoryCustom {
}
