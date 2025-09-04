package com.pinHouse.server.platform.notice.application.usecase;

import com.pinHouse.server.platform.notice.application.dto.response.NoticeSupplyDTO;

public interface DepositUseCase {

    /// 시뮬레이터
    NoticeSupplyDTO.NoticeLeaseOptionResponse getLeaseByPercent(String noticeId, String type, double percentage);


}
