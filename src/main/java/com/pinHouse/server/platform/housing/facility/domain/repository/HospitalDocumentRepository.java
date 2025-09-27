package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HospitalDocumentRepository extends MongoRepository<Hospital, String> {
}
