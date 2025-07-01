package com.myhome.server.platform.adapter.out.mongo.notice;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {
}
