package co.kr.pinhouse.domain.cs.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.CsErrorCode;
import co.kr.pinhouse.common.exception.code.UserErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.application.usecase.AdminSessionUseCase;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryMessageRequest;
import co.kr.pinhouse.domain.cs.application.dto.request.CreateCsInquiryRequest;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryDetailResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryMessageResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquiryRequesterResponse;
import co.kr.pinhouse.domain.cs.application.dto.response.CsInquirySummaryResponse;
import co.kr.pinhouse.domain.cs.application.usecase.CsInquiryUseCase;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiry;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryCategory;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryMessage;
import co.kr.pinhouse.domain.cs.domain.entity.CsInquiryStatus;
import co.kr.pinhouse.domain.cs.domain.entity.CsMessageSenderType;
import co.kr.pinhouse.domain.cs.domain.repository.CsInquiryMessageRepository;
import co.kr.pinhouse.domain.cs.domain.repository.CsInquiryRepository;
import co.kr.pinhouse.domain.user.domain.entity.User;
import co.kr.pinhouse.domain.user.domain.repository.UserJpaRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CsInquiryService implements CsInquiryUseCase {

	private final UserJpaRepository userRepository;
	private final CsInquiryRepository inquiryRepository;
	private final CsInquiryMessageRepository messageRepository;
	private final AdminSessionUseCase adminSessionService;
	private final AdminAuditLogUseCase adminAuditLogService;

	// =================
	//  사용자 로직
	// =================

	/// 사용자 문의 생성
	@Transactional
	@Override
	public CsInquiryDetailResponse createInquiry(UUID userId, CreateCsInquiryRequest request) {
		User user = loadUser(userId);
		CsInquiry inquiry = inquiryRepository.save(CsInquiry.create(user, request.category(), request.title()));
		messageRepository.save(CsInquiryMessage.of(inquiry, CsMessageSenderType.USER, userId, request.content()));

		return toDetail(inquiry, true);
	}

	/// 내 문의 목록 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<CsInquirySummaryResponse> getMyInquiries(UUID userId, SliceRequest sliceRequest) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = inquiryRepository.findByUser_IdOrderByCreatedAtDesc(userId, pageable);

		return SliceResponse.from(page.map(CsInquirySummaryResponse::from), page.getTotalElements());
	}

	/// 내 문의 상세 조회
	@Transactional(readOnly = true)
	@Override
	public CsInquiryDetailResponse getMyInquiry(UUID userId, Long inquiryId) {
		CsInquiry inquiry = loadInquiry(inquiryId);
		validateOwner(inquiry, userId);
		return toDetail(inquiry, false);
	}

	/// 사용자 추가 메시지 등록
	@Transactional
	@Override
	public CsInquiryDetailResponse addUserMessage(
		UUID userId,
		Long inquiryId,
		CreateCsInquiryMessageRequest request
	) {
		CsInquiry inquiry = loadInquiry(inquiryId);
		validateOwner(inquiry, userId);

		messageRepository.save(CsInquiryMessage.of(inquiry, CsMessageSenderType.USER, userId, request.content()));
		inquiry.markUserMessage();

		return toDetail(inquiry, false);
	}

	// =================
	//  관리자 로직
	// =================

	/// 관리자 문의 목록 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<CsInquirySummaryResponse> getAdminInquiries(
		CsInquiryStatus status,
		CsInquiryCategory category,
		SliceRequest sliceRequest
	) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
		var page = inquiryRepository.findAll(buildSpecification(status, category), pageable);

		return SliceResponse.from(page.map(CsInquirySummaryResponse::from), page.getTotalElements());
	}

	/// 관리자 문의 상세 조회
	@Transactional(readOnly = true)
	@Override
	public CsInquiryDetailResponse getAdminInquiry(Long inquiryId) {
		return toDetail(loadInquiry(inquiryId), true);
	}

	/// 문의 담당 관리자 지정
	@Transactional
	@Override
	public CsInquiryDetailResponse assignInquiry(
		Long inquiryId,
		UUID assigneeAdminId,
		UUID actorAdminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(actorAdminId);
		adminSessionService.loadAdmin(assigneeAdminId);

		CsInquiry inquiry = loadInquiry(inquiryId);
		CsInquirySummaryResponse before = CsInquirySummaryResponse.from(inquiry);
		inquiry.assignAdmin(assigneeAdminId);
		CsInquiryDetailResponse after = toDetail(inquiry, true);

		adminAuditLogService.log(
			actorAdminId,
			AdminAuditActionType.ASSIGN,
			AdminAuditTargetType.CS_INQUIRY,
			String.valueOf(inquiryId),
			"CS 문의 담당자 지정",
			before,
			CsInquirySummaryResponse.from(inquiry),
			httpServletRequest
		);

		return after;
	}

	/// 문의 상태 변경
	@Transactional
	@Override
	public CsInquiryDetailResponse updateStatus(
		Long inquiryId,
		CsInquiryStatus status,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);

		CsInquiry inquiry = loadInquiry(inquiryId);
		CsInquirySummaryResponse before = CsInquirySummaryResponse.from(inquiry);
		inquiry.changeStatus(status);
		CsInquiryDetailResponse after = toDetail(inquiry, true);

		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.STATUS_CHANGE,
			AdminAuditTargetType.CS_INQUIRY,
			String.valueOf(inquiryId),
			"CS 문의 상태 변경",
			before,
			CsInquirySummaryResponse.from(inquiry),
			httpServletRequest
		);

		return after;
	}

	/// 관리자 답변 등록
	@Transactional
	@Override
	public CsInquiryDetailResponse addAdminMessage(
		Long inquiryId,
		UUID adminId,
		CreateCsInquiryMessageRequest request,
		HttpServletRequest httpServletRequest
	) {
		adminSessionService.loadAdmin(adminId);

		CsInquiry inquiry = loadInquiry(inquiryId);
		int beforeCount = messageRepository.findByInquiry_IdOrderByCreatedAtAsc(inquiryId).size();

		messageRepository.save(CsInquiryMessage.of(inquiry, CsMessageSenderType.ADMIN, adminId, request.content()));
		inquiry.markAdminResponse();
		CsInquiryDetailResponse after = toDetail(inquiry, true);

		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.REPLY,
			AdminAuditTargetType.CS_INQUIRY,
			String.valueOf(inquiryId),
			"CS 문의 답변 등록",
			java.util.Map.of("messageCount", beforeCount),
			java.util.Map.of("messageCount", beforeCount + 1),
			httpServletRequest
		);

		return after;
	}

	// =================
	//  내부 로직
	// =================

	/// 문의 검색 조건 Specification 생성
	private Specification<CsInquiry> buildSpecification(CsInquiryStatus status, CsInquiryCategory category) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new java.util.ArrayList<>();
			if (status != null) {
				predicates.add(criteriaBuilder.equal(root.get("status"), status));
			}
			if (category != null) {
				predicates.add(criteriaBuilder.equal(root.get("category"), category));
			}
			return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
		};
	}

	/// 문의 단건 조회
	private CsInquiry loadInquiry(Long inquiryId) {
		return inquiryRepository.findById(inquiryId)
			.orElseThrow(() -> new CustomException(CsErrorCode.NOT_FOUND_INQUIRY));
	}

	/// 사용자 단건 조회
	private User loadUser(UUID userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
	}

	/// 문의 소유자 검증
	private void validateOwner(CsInquiry inquiry, UUID userId) {
		if (!inquiry.getUser().getId().equals(userId)) {
			throw new CustomException(CsErrorCode.FORBIDDEN_INQUIRY_ACCESS);
		}
	}

	/// 문의 상세 응답 DTO 변환
	private CsInquiryDetailResponse toDetail(CsInquiry inquiry, boolean includeRequester) {
		List<CsInquiryMessageResponse> messages = messageRepository.findByInquiry_IdOrderByCreatedAtAsc(inquiry.getId())
			.stream()
			.map(CsInquiryMessageResponse::from)
			.toList();

		CsInquiryRequesterResponse requester = includeRequester
			? CsInquiryRequesterResponse.builder()
				.userId(inquiry.getUser().getId())
				.maskedName(maskName(inquiry.getUser().getName()))
				.maskedEmail(maskEmail(inquiry.getUser().getEmail()))
				.build()
			: null;

		return CsInquiryDetailResponse.of(inquiry, requester, messages);
	}

	/// 이름 마스킹
	private String maskName(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		if (name.length() == 1) {
			return "*";
		}
		if (name.length() == 2) {
			return name.charAt(0) + "*";
		}
		return name.charAt(0) + "*" + name.charAt(name.length() - 1);
	}

	/// 이메일 마스킹
	private String maskEmail(String email) {
		if (email == null || email.isBlank() || !email.contains("@")) {
			return null;
		}
		String[] parts = email.split("@", 2);
		String local = parts[0];
		String domain = parts[1];

		if (local.length() <= 2) {
			return local.charAt(0) + "***@" + domain;
		}
		return local.substring(0, 2) + "***@" + domain;
	}
}
