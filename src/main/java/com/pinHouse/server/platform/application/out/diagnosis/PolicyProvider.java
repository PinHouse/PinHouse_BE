package com.pinHouse.server.platform.application.out.diagnosis;

import com.pinHouse.server.platform.application.service.SubscriptionAccount;
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

    int marriedYouthAgeMin();

    int elderAgeMin();

    int recommendHousingSize(RegionCode region, SubscriptionAccount subscriptionAccount, long deposit);

    int reApplyBanMonths();
}
