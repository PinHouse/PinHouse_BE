package co.kr.pinhouse.domain.cs.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;

public interface CsInquiryRepository extends JpaRepository<CsInquiry, Long>, JpaSpecificationExecutor<CsInquiry> {

	Page<CsInquiry> findByUser_IdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

	long countByUser_Id(UUID userId);

	long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

	long countByStatusNotIn(List<CsInquiryStatus> statuses);

	long countByStatusInAndLastMessageAtBefore(List<CsInquiryStatus> statuses, LocalDateTime threshold);

	List<CsInquiry> findTop5ByOrderByCreatedAtDesc();

	@EntityGraph(attributePaths = "user")
	List<CsInquiry> findTop5ByOrderByLastMessageAtDescIdDesc();
}
