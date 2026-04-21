package co.kr.pinhouse.domain.admin.audit.application.service;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.audit.application.dto.response.AdminAuditLogResponse;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditLog;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import co.kr.pinhouse.domain.admin.audit.domain.repository.AdminAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuditLogService implements AdminAuditLogUseCase {

	private final AdminAuditLogRepository repository;
	private final ObjectMapper objectMapper;

	// =================
	//  외부 로직
	// =================

	/// 관리자 감사로그 저장
	@Transactional
	@Override
	public void log(
		UUID adminId,
		AdminAuditActionType actionType,
		AdminAuditTargetType targetType,
		String targetId,
		String summary,
		Object beforeState,
		Object afterState,
		HttpServletRequest request
	) {
		repository.save(AdminAuditLog.of(
			adminId,
			actionType,
			targetType,
			targetId,
			summary,
			toJson(beforeState),
			toJson(afterState),
			extractClientIp(request),
			request != null ? request.getHeader("User-Agent") : null
		));
	}

	// =================
	//  퍼블릭 로직
	// =================

	/// 전체 감사로그 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<AdminAuditLogResponse> getLogs(SliceRequest sliceRequest) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = repository.findAllByOrderByCreatedAtDesc(pageable);

		return SliceResponse.from(page.map(AdminAuditLogResponse::from), page.getTotalElements());
	}

	/// 대상별 감사로그 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<AdminAuditLogResponse> getLogsByTarget(
		AdminAuditTargetType targetType,
		String targetId,
		SliceRequest sliceRequest
	) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = repository.findByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId, pageable);

		return SliceResponse.from(page.map(AdminAuditLogResponse::from), page.getTotalElements());
	}

	// =================
	//  내부 로직
	// =================

	/// 감사로그 상태 JSON 직렬화
	private String toJson(Object value) {
		if (value == null) {
			return null;
		}

		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			log.warn("감사 로그 직렬화 실패 - type={}", sanitize(value.getClass().getName()), e);
			return null;
		}
	}

	/// 요청 IP 추출
	private String extractClientIp(HttpServletRequest request) {
		if (request == null) {
			return null;
		}

		String forwardedFor = request.getHeader("X-Forwarded-For");
		if (forwardedFor != null && !forwardedFor.isBlank() && !"unknown".equalsIgnoreCase(forwardedFor)) {
			return forwardedFor.split(",")[0].trim();
		}

		String realIp = request.getHeader("X-Real-IP");
		if (realIp != null && !realIp.isBlank() && !"unknown".equalsIgnoreCase(realIp)) {
			return realIp;
		}

		return request.getRemoteAddr();
	}
}
