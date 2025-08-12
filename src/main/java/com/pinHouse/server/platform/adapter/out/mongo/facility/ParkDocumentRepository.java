package com.pinHouse.server.platform.adapter.out.mongo.facility;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkDocumentRepository extends MongoRepository<ParkDocument, String> {
}
