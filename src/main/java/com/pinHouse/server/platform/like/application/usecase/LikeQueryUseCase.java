package com.pinHouse.server.platform.like.application.usecase;

import java.util.List;
import java.util.UUID;

public interface LikeQueryUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    List<String> getLikeNoticeIds(UUID userId);

    List<String> getLikeUnitTypeIds(UUID userId);


}
