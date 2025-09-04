package com.pinHouse.server.platform.housingFit.rule.application.usecase;

import com.pinHouse.server.platform.housingFit.rule.application.service.SubscriptionAccount;
import com.pinHouse.server.platform.housingFit.rule.application.service.RegionCode;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import com.pinHouse.server.platform.housingFit.school.domain.entity.Region;

public interface PolicyProvider {

    int requiredLocalResidencyMonths(Region region);

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
