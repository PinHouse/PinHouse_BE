package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.util.*;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String> {

    // 아이디 조회
    @Query("{ 'noticeId' : ?0}")
    Optional<NoticeDocument> findById(String id);

    // 마감 임박: 오늘 이후만
    Page<NoticeDocument> findByApplyEndGreaterThanEqual(Instant now, Pageable pageable);

    // 최신 공고: 오늘까지 공개된 것만
    Page<NoticeDocument> findByAnnounceDateLessThanEqual(Instant now, Pageable pageable);

    /// 아이디 목록 조회
    List<NoticeDocument> findByIdIn(List<String> noticeIds);
}
