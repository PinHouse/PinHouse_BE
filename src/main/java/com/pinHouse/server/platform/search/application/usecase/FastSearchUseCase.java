package com.pinHouse.server.platform.search.application.usecase;

import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.application.dto.FastSearchResponse;

import java.util.UUID;

public interface FastSearchUseCase {

    /// 빠른 검색
    FastSearchResponse search(UUID userId, FastSearchRequest request);

}
