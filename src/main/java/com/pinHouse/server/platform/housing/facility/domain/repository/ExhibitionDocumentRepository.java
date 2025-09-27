package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.Exhibition;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExhibitionDocumentRepository extends MongoRepository<Exhibition, String> {

}
