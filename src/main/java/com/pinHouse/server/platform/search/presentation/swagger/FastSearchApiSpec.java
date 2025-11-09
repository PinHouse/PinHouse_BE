package com.pinHouse.server.platform.search.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.application.dto.FastSearchResponse;
import com.pinHouse.server.platform.search.application.dto.SearchHistoryResponse;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "빠른 검색 API", description = "빠른 검색을 지원하는 API 입니다.")
public interface FastSearchApiSpec {

    @Operation(
            summary = "빠른 검색 API",
            description = "빠른 검색 API 입니다."
    )
    ApiResponse<FastSearchResponse> search(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody FastSearchRequest request);


    @Operation(
            summary = "빠른 검색 존재여부 API",
            description = "제일 최근 빠른 검색 존재여부 API 입니다."
    )
    ApiResponse<SearchHistoryResponse> history(@AuthenticationPrincipal PrincipalDetails principalDetails);

}
