package com.pinHouse.domain.search;

import java.util.UUID;

import org.springframework.web.bind.annotation.*;

import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.search.application.dto.FastSearchRequest;
import com.pinHouse.domain.search.application.dto.FastSearchResponse;
import com.pinHouse.domain.search.application.dto.SearchHistoryResponse;
import com.pinHouse.domain.search.application.usecase.FastSearchUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search/fast")
public class FastSearchApi implements FastSearchApiSpec {

	private final FastSearchUseCase service;

	/**
	 * 검색 기록이 있는지 여부 체크
	 * @param principalDetails
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
