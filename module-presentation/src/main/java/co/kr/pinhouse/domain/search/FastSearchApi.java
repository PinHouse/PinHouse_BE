package co.kr.pinhouse.domain.search;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.kr.pinhouse.common.auth.CurrentUserId;
import co.kr.pinhouse.common.response.ApiResponse;
import co.kr.pinhouse.domain.search.application.dto.request.FastSearchRequest;
import co.kr.pinhouse.domain.search.application.dto.response.FastSearchResponse;
import co.kr.pinhouse.domain.search.application.dto.response.SearchHistoryResponse;
import co.kr.pinhouse.domain.search.application.usecase.FastSearchUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search/fast")
public class FastSearchApi implements FastSearchApiSpec {

	private final FastSearchUseCase service;

	/**
	 * 검색 기록이 있는지 여부 체크
	 * @return
	 */
	@GetMapping("/history")
	public ApiResponse<SearchHistoryResponse> history(@CurrentUserId(required = true) UUID userId) {

		/// 서비스
		var response = service.searchHistory(userId);

		/// 응답
		return ApiResponse.ok(response);
	}

	/**
	 * 빠른 검색 API
	 *
	 * @param request 요청 DTO
	 */
	@PostMapping
	public ApiResponse<FastSearchResponse> search(
		@CurrentUserId(required = true) UUID userId,
		@RequestBody @Valid FastSearchRequest request) {

		/// 서비스
		var response = service.search(userId, request);

		/// 응답
		return ApiResponse.ok(response);
	}

}
