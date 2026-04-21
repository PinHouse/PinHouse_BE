package co.kr.pinhouse.domain.admin.notice.application.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.NoticeErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.common.response.pageable.SliceRequest;
import co.kr.pinhouse.common.response.pageable.SliceResponse;
import co.kr.pinhouse.domain.admin.audit.application.usecase.AdminAuditLogUseCase;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditActionType;
import co.kr.pinhouse.domain.admin.audit.domain.entity.AdminAuditTargetType;
import co.kr.pinhouse.domain.admin.notice.application.dto.request.UpdateAdminNoticeRequest;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeResponse;
import co.kr.pinhouse.domain.admin.notice.application.dto.response.AdminNoticeSummaryResponse;
import co.kr.pinhouse.domain.admin.notice.application.usecase.AdminNoticeUseCase;
import co.kr.pinhouse.domain.admin.notice.domain.entity.NoticeAdminOverride;
import co.kr.pinhouse.domain.admin.notice.domain.repository.NoticeAdminOverrideRepository;
import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.housing.notice.domain.repository.NoticeDocumentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminNoticeService implements AdminNoticeUseCase {

	private final NoticeDocumentRepository noticeRepository;
	private final NoticeAdminOverrideRepository overrideRepository;
	private final AdminAuditLogUseCase adminAuditLogService;

	// =================
	//  퍼블릭 로직
	// =================

	/// 관리자 공고 목록 조회
	@Transactional(readOnly = true)
	@Override
	public SliceResponse<AdminNoticeSummaryResponse> getNotices(String keyword, SliceRequest sliceRequest) {
		var pageable = PageRequest.of(sliceRequest.page() - 1, sliceRequest.offSet(),
			Sort.by(Sort.Order.desc("announceDate"), Sort.Order.desc("noticeId")));
		Page<NoticeDocument> page = loadNoticePage(keyword, pageable);

		Map<String, NoticeAdminOverride> overrideMap = overrideRepository.findByNoticeIdIn(
				page.getContent().stream().map(NoticeDocument::getId).toList())
			.stream()
			.collect(Collectors.toMap(NoticeAdminOverride::getNoticeId, Function.identity()));

		return SliceResponse.from(page.map(notice ->
			AdminNoticeSummaryResponse.from(notice, overrideMap.get(notice.getId()))), page.getTotalElements());
	}

	/// 관리자 공고 상세 조회
	@Transactional(readOnly = true)
	@Override
	public AdminNoticeResponse getNotice(String noticeId) {
		NoticeDocument notice = loadNotice(noticeId);
		NoticeAdminOverride override = overrideRepository.findByNoticeId(noticeId).orElse(null);

		return AdminNoticeResponse.from(notice, override);
	}

	/// 관리자 공고 운영 정보 수정
	@Transactional
	@Override
	public AdminNoticeResponse updateNotice(
		String noticeId,
		UpdateAdminNoticeRequest request,
		UUID adminId,
		HttpServletRequest httpServletRequest
	) {
		NoticeDocument notice = loadNotice(noticeId);
		NoticeAdminOverride override = overrideRepository.findByNoticeId(noticeId)
			.orElseGet(() -> NoticeAdminOverride.create(noticeId));
		AdminNoticeResponse before = AdminNoticeResponse.from(notice, override.getId() == null ? null : override);

		override.apply(request);
		NoticeAdminOverride savedOverride = overrideRepository.save(override);

		AdminNoticeResponse after = AdminNoticeResponse.from(notice, savedOverride);

		adminAuditLogService.log(
			adminId,
			AdminAuditActionType.UPDATE,
			AdminAuditTargetType.NOTICE,
			noticeId,
			"공고 운영 정보 수정",
			before,
			after,
			httpServletRequest
		);

		return after;
	}

	// =================
	//  내부 로직
	// =================

	/// 검색 조건에 맞는 공고 페이지 조회
	private Page<NoticeDocument> loadNoticePage(String keyword, PageRequest pageable) {
		if (keyword == null || keyword.isBlank()) {
			return noticeRepository.findAll(pageable);
		}

		Instant now = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toInstant();
		return noticeRepository.searchByTitle(keyword, pageable, false, now);
	}

	/// 공고 단건 조회
	private NoticeDocument loadNotice(String noticeId) {
		return noticeRepository.findById(noticeId)
			.orElseThrow(() -> new CustomException(NoticeErrorCode.NOT_FOUND_NOTICE));
	}
}
