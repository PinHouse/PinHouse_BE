package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {
    Optional<NoticeDocument> findByNoticeId(String noticeId);
}
