package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.*;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {
    Optional<NoticeDocument> findByNoticeId(String noticeId);

    // 마감 임박: 오늘 이후만
    Page<NoticeDocument> findByApplyEndGreaterThanEqual(Instant now, Pageable pageable);

    // 최신 공고: 오늘까지 공개된 것만
    Page<NoticeDocument> findByAnnounceDateLessThanEqual(Instant now, Pageable pageable);

    /// 아이디 목록 조회
    List<NoticeDocument> findByNoticeIdIn(List<String> noticeIds);
}
