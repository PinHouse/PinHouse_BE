package com.pinHouse.domain.search;

import java.util.UUID;

import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.search.application.dto.FastSearchRequest;
import com.pinHouse.domain.search.application.dto.FastSearchResponse;
import com.pinHouse.domain.search.application.dto.SearchHistoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "빠른 검색 API", description = "빠른 검색을 지원하는 API 입니다.")
public interface FastSearchApiSpec {

    @Operation(
            summary = "빠른 검색 API",
            description = "빠른 검색 API 입니다."
    )
    ApiResponse<FastSearchResponse> search(
            @CurrentUserId(required = true) UUID userId,
            @RequestBody FastSearchRequest request);


    @Operation(
            summary = "빠른 검색 존재여부 API",
            description = "제일 최근 빠른 검색 존재여부 API 입니다."
    )
    ApiResponse<SearchHistoryResponse> history(@CurrentUserId(required = true) UUID userId);

}
