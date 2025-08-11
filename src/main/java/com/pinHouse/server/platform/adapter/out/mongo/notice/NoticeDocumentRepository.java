package com.pinHouse.server.platform.adapter.out.mongo.notice;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {

    Optional<NoticeDocument> findByNoticeId(String noticeId);

}
