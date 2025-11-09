package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.*;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String>, NoticeDocumentRepositoryCustom {

    // 아이디 조회
    @Query("{ 'noticeId' : ?0}")
    Optional<NoticeDocument> findById(String id);

    /// 아이디 목록 조회
    List<NoticeDocument> findByIdIn(List<String> noticeIds);
}
