package com.pinHouse.server.platform.housing.deposit.application.usecase;

import com.pinHouse.server.platform.housing.deposit.application.dto.NoticeLeaseOptionResponse;

public interface DepositUseCase {

    /// 시뮬레이터
    NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, String type, double percentage);


}
