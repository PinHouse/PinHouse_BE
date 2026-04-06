package com.pinHouse.domain.housing.notice.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.pinHouse.domain.housing.notice.domain.entity.NoticeDocument;

public interface NoticeDocumentRepository extends MongoRepository<NoticeDocument, String>, NoticeDocumentRepositoryCustom {

	// 아이디 조회
	@Query("{ 'noticeId' : ?0}")
	Optional<NoticeDocument> findById(String id);

	/// 아이디 목록 조회
	List<NoticeDocument> findByIdIn(List<String> noticeIds);
}
