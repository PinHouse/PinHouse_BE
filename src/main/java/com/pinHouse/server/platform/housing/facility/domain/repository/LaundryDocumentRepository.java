package com.pinHouse.server.platform.housing.facility.domain.repository;

import com.pinHouse.server.platform.housing.facility.domain.entity.Exhibition;
import com.pinHouse.server.platform.housing.facility.domain.entity.Laundry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LaundryDocumentRepository extends MongoRepository<Laundry, String> {
}
