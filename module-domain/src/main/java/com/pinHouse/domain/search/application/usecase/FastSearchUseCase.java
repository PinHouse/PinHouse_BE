package com.pinHouse.domain.search.application.usecase;

import com.pinHouse.domain.search.application.dto.FastSearchRequest;
import com.pinHouse.domain.search.application.dto.FastSearchResponse;
import com.pinHouse.domain.search.application.dto.SearchHistoryResponse;

import java.util.UUID;

public interface FastSearchUseCase {

    /// 기존에 검색 기록이 있는지
    SearchHistoryResponse searchHistory(UUID userId);

    /// 빠른 검색
    FastSearchResponse search(UUID userId, FastSearchRequest request);

}
