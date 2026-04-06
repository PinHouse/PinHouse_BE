package co.kr.pinhouse.domain.housing.facility.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.kr.pinhouse.domain.housing.facility.domain.entity.FacilityStatDocument;

public interface FacilityStatDocumentRepository extends MongoRepository<FacilityStatDocument, String>, FacilityStatDocumentRepositoryCustom {
}
