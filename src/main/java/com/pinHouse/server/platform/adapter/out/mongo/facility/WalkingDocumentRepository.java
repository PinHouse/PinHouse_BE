package com.pinHouse.server.platform.adapter.out.mongo.facility;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WalkingDocumentRepository extends MongoRepository<WalkingDocument, String> {
}
