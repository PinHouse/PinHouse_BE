package co.kr.pinhouse.domain.search.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.domain.search.application.dto.request.FastSearchRequest;
import co.kr.pinhouse.domain.search.application.dto.response.FastSearchResponse;
import co.kr.pinhouse.domain.search.application.dto.response.SearchHistoryResponse;

public interface FastSearchUseCase {

	/// 기존에 검색 기록이 있는지
	SearchHistoryResponse searchHistory(UUID userId);

	/// 빠른 검색
	FastSearchResponse search(UUID userId, FastSearchRequest request);

}
