package com.pinHouse.server.platform.adapter.out;

import com.pinHouse.server.platform.application.out.diagnosis.PolicyProvider;
import com.pinHouse.server.platform.application.service.SubscriptionAccount;
import com.pinHouse.server.platform.application.service.RegionCode;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;

public class InMemoryPolicyProvider implements PolicyProvider {

    public int requiredLocalResidencyMonths(RegionCode region) {

        /// 지역마다 거주 필요한 기간이 다르다.
        return region == RegionCode.NON_SUDO ? 6 : 12;
    }


    public double maxIncomeRatio(SupplyType type, int familyCount) {
        double base = switch (type) {
            case YOUTH_SPECIAL -> 100.0;
            case NEWCOUPLE_SPECIAL -> 120.0;
            case ELDER_SPECIAL, ELDER_SUPPORT_SPECIAL -> 100.0;
            case MULTICHILD_SPECIAL -> 130.0;
            default -> 150.0;
        };

        return base + Math.max(0, familyCount - 3) * 5.0;
    }

    public long maxFinancialAsset(SupplyType type, int familyCount) {
        return 300_000_000L + (long) Math.max(0, familyCount - 3) * 30_000_000L;
    }

    public long maxCarValue(SupplyType type) { return 40_000_000L; }

    public int youthAgeMax() {
        return 39;
    }

    public int youthAgeMin() {
        return 19;
    }

    /// 결혼 여부는 7년으로 지정
    public int newlyMarriedMaxYears() {
        return 7;
    }

    public int marriedYouthAgeMin() {
        return 6;
    }

    public int elderAgeMin() {
        return 65;
    }

    public int recommendHousingSize(RegionCode region, SubscriptionAccount subscriptionAccount, long deposit) {

        // 국민주택 청약저축은 85㎡ 이하만 가능
        if (subscriptionAccount == SubscriptionAccount.UPPER_600) {
            return 85;
        }

        // 지역별 최소 예치금 기준
        int[][] regionLimits; // 각 행: [85㎡, 102㎡, 135㎡, 기타]

        switch (region) {
            case SEOUL:
            case INCHEON:
            case GYEONGGI:
            case BUSAN:
                regionLimits = new int[][] { {85, 3_000_000}, {102, 6_000_000}, {135, 10_000_000}, {Integer.MAX_VALUE, 15_000_000} };
                break;
            case DAEGU:
            case GWANGJU:
            case DAEJEON:
            case ULSAN:
                regionLimits = new int[][] { {85, 2_500_000}, {102, 4_000_000}, {135, 7_000_000}, {Integer.MAX_VALUE, 10_000_000} };
                break;
            default:
                regionLimits = new int[][] { {85, 2_000_000}, {102, 3_000_000}, {135, 4_000_000}, {Integer.MAX_VALUE, 5_000_000} };
        }

        // 예치금으로 가능한 최대 주택 규모 찾기
        int recommendedSize = 0;
        for (int[] limit : regionLimits) {
            int size = limit[0];
            int minDeposit = limit[1];
            if (deposit >= minDeposit) {
                recommendedSize = size;
            } else {
                break;
            }
        }

        return recommendedSize;
    }



    public int reApplyBanMonths() {
        return 24;
    }
}



