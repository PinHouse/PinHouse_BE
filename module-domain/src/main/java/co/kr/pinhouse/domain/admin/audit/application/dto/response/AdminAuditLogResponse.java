package co.kr.pinhouse.domain.admin.audit.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditLog;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import lombok.Builder;

@Builder
public record AdminAuditLogResponse(
	Long id,
	UUID adminId,
	AdminAuditActionType actionType,
	AdminAuditTargetType targetType,
	String targetId,
	String summary,
	String beforeJson,
	String afterJson,
	String ipAddress,
	String userAgent,
	LocalDateTime createdAt
) {

	public static AdminAuditLogResponse from(AdminAuditLog log) {
		return AdminAuditLogResponse.builder()
			.id(log.getId())
			.adminId(log.getAdminId())
			.actionType(log.getActionType())
			.targetType(log.getTargetType())
			.targetId(log.getTargetId())
			.summary(log.getSummary())
			.beforeJson(log.getBeforeJson())
			.afterJson(log.getAfterJson())
			.ipAddress(log.getIpAddress())
			.userAgent(log.getUserAgent())
			.createdAt(log.getCreatedAt())
			.build();
	}
}
