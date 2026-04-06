package com.pinHouse.domain.search.application.usecase;

import java.util.UUID;

import com.pinHouse.domain.search.application.dto.FastSearchRequest;
import com.pinHouse.domain.search.application.dto.FastSearchResponse;
import com.pinHouse.domain.search.application.dto.SearchHistoryResponse;

public interface FastSearchUseCase {

	/// 기존에 검색 기록이 있는지
	SearchHistoryResponse searchHistory(UUID userId);

	/// 빠른 검색
	FastSearchResponse search(UUID userId, FastSearchRequest request);

}
