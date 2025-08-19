package com.pinHouse.server.platform.application.out.diagnosis;

import com.pinHouse.server.platform.application.service.AccountType;
import com.pinHouse.server.platform.application.service.RegionCode;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;

public interface PolicyProvider {

    int requiredLocalResidencyMonths(RegionCode region);

    double maxIncomeRatio(SupplyType type, int familyCount);

    long maxFinancialAsset(SupplyType type, int familyCount);

    long maxCarValue(SupplyType type);

    int youthAgeMax();

    int youthAgeMin();

    int newlyMarriedMaxYears();

    int elderAgeMin();

    long minDepositForHousePrice(long housePrice, RegionCode region, AccountType accountType);

    int reApplyBanMonths();
}
