package co.kr.pinhouse.domain.admin.audit.domain.entity;

import java.util.UUID;

import co.kr.pinhouse.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin_audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminAuditLog extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "admin_id", nullable = false, columnDefinition = "BINARY(16)")
	private UUID adminId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private AdminAuditActionType actionType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private AdminAuditTargetType targetType;

	@Column(nullable = false)
	private String targetId;

	@Column(nullable = false)
	private String summary;

	@Column(name = "before_json", columnDefinition = "TEXT")
	private String beforeJson;

	@Column(name = "after_json", columnDefinition = "TEXT")
	private String afterJson;

	@Column(name = "ip_address", length = 100)
	private String ipAddress;

	@Column(name = "user_agent", columnDefinition = "TEXT")
	private String userAgent;

	@Builder
	protected AdminAuditLog(
		UUID adminId,
		AdminAuditActionType actionType,
		AdminAuditTargetType targetType,
		String targetId,
		String summary,
		String beforeJson,
		String afterJson,
		String ipAddress,
		String userAgent
	) {
		this.adminId = adminId;
		this.actionType = actionType;
		this.targetType = targetType;
		this.targetId = targetId;
		this.summary = summary;
		this.beforeJson = beforeJson;
		this.afterJson = afterJson;
		this.ipAddress = ipAddress;
		this.userAgent = userAgent;
	}

	public static AdminAuditLog of(
		UUID adminId,
		AdminAuditActionType actionType,
		AdminAuditTargetType targetType,
		String targetId,
		String summary,
		String beforeJson,
		String afterJson,
		String ipAddress,
		String userAgent
	) {
		return AdminAuditLog.builder()
			.adminId(adminId)
			.actionType(actionType)
			.targetType(targetType)
			.targetId(targetId)
			.summary(summary)
			.beforeJson(beforeJson)
			.afterJson(afterJson)
			.ipAddress(ipAddress)
			.userAgent(userAgent)
			.build();
	}
}
