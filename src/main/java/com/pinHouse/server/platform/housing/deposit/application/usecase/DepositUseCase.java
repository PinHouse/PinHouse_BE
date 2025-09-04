package com.pinHouse.server.platform.housing.deposit.application.usecase;

import com.pinHouse.server.platform.housing.deposit.application.dto.response.NoticeSupplyDTO;

public interface DepositUseCase {

    /// 시뮬레이터
    NoticeSupplyDTO.NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, String type, double percentage);


}
