package com.pinHouse.server.platform.notice.domain.repository;

import com.pinHouse.server.platform.notice.domain.entity.Notice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NoticeDocumentRepository extends MongoRepository<Notice, String> {

    Optional<Notice> findByNoticeId(String noticeId);

}
