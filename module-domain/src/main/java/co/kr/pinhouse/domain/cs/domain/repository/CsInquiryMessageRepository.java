package co.kr.pinhouse.domain.cs.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryMessage;

public interface CsInquiryMessageRepository extends JpaRepository<CsInquiryMessage, Long> {

	List<CsInquiryMessage> findByInquiry_IdOrderByCreatedAtAsc(Long inquiryId);
}
