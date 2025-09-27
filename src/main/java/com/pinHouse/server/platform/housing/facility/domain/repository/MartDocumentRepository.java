package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.Mart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MartDocumentRepository extends MongoRepository<Mart, String> {

}
