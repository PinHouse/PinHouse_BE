package co.kr.pinhouse.domain.cs.domain.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;

public interface CsInquiryRepository extends JpaRepository<CsInquiry, Long>, JpaSpecificationExecutor<CsInquiry> {

	Page<CsInquiry> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

	long countByUser_Id(UUID userId);
}
