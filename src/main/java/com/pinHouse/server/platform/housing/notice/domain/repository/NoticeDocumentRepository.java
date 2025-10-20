package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {
    Optional<NoticeDocument> findByNoticeId(String noticeId);

    /// 아이디 목록 조회
    List<NoticeDocument> findByNoticeIdIn(List<String> noticeIds);
}
