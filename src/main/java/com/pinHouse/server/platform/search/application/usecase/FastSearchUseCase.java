package com.pinHouse.server.platform.search.application.usecase;

import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;

import java.util.UUID;
import java.util.List;

public interface FastSearchUseCase {

    /// 빠른 검색
    List<UnitType> search(UUID userId, FastSearchRequest request);

}
