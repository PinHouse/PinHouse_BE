package com.pinHouse.server.platform.diagnostic.rule.application.usecase;

import com.pinHouse.server.platform.diagnostic.rule.domain.entity.RentalType;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;

public interface PolicyUseCase {


    /// 소득 비율
    double maxIncomeRatio(SupplyType supply, RentalType rental, int familyCount);

    /// 전체 재산 관련 부분
    long maxTotalAsset(SupplyType supply, RentalType rental, int familyCount);

    /// 자동차 재산 관련 부분
    long checkMaxCarValue();

    /// 자녀 나이 관련
    int youthAgeMin();

    /// 신혼부부 관련
    int newlyMarriedMaxYears();

    /// 미성년자 관련
    int marriedYouthAgeMin();

    /// 고령자 관련
    int elderAge();

    /// 무주택 기간
    int reApplyBanMonths(RentalType rental);
}

