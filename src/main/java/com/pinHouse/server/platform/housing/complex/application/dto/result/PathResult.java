package com.pinHouse.server.platform.housing.complex.application.dto.result;

import java.util.List;

public interface PathResult {

    int searchType();

    List<RootResult> routes();
}
