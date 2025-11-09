package com.pinHouse.server.platform.search.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.application.dto.FastSearchResponse;
import com.pinHouse.server.platform.search.application.dto.SearchHistoryResponse;
import com.pinHouse.server.platform.search.application.usecase.FastSearchUseCase;
import com.pinHouse.server.platform.search.presentation.swagger.FastSearchApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<SearchHistoryResponse> history(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스
        var response = service.searchHistory(principalDetails.getId());

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
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid FastSearchRequest request) {

        /// 서비스
        var response = service.search(principalDetails.getId(), request);

        /// 응답
        return ApiResponse.ok(response);
    }


}
