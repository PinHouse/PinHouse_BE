package co.kr.pinhouse.domain.admin.audit.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditLog;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {

	Page<AdminAuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

	Page<AdminAuditLog> findByTargetTypeAndTargetIdOrderByCreatedAtDesc(
		AdminAuditTargetType targetType,
		String targetId,
		Pageable pageable
	);
}
