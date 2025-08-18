package com.pinHouse.server.platform.adapter.out;

import com.pinHouse.server.platform.application.out.diagnosis.PolicyProvider;
import com.pinHouse.server.platform.application.service.AccountType;
import com.pinHouse.server.platform.application.service.RegionCode;
import com.pinHouse.server.platform.domain.diagnosis.model.SupplyType;

public class InMemoryPolicyProvider implements PolicyProvider {
    public int requiredLocalResidencyMonths(RegionCode region) {
        // 예시: 수도권 12개월, 기타 6개월
        return region == RegionCode.SUDO ? 12 : 6;
    }
    public double maxIncomeRatio(SupplyType type, int familyCount) {
        // Placeholder: 실제 표준은 DB/YAML로 관리 권장
        double base = switch (type) {
            case YOUTH_SPECIAL -> 100.0;
            case NEWCOUPLE_SPECIAL -> 120.0;
            case ELDER_SPECIAL, ELDER_SUPPORT_SPECIAL -> 100.0;
            case MULTICHILD_SPECIAL -> 130.0;
            default -> 150.0; // 일반
        };
        // 가구원에 따른 보정(예시)
        return base + Math.max(0, familyCount - 3) * 5.0;
    }
    public long maxFinancialAsset(SupplyType type, int familyCount) {
        return 300_000_000L + (long) Math.max(0, familyCount - 3) * 30_000_000L;
    }
    public long maxCarValue(SupplyType type) { return 40_000_000L; }
    public int youthAgeMax() { return 39; }
    public int youthAgeMin() { return 19; }
    public int newlyMarriedMaxYears() { return 7; }
    public int elderAgeMin() { return 65; }
    public long minDepositForHousePrice(long housePrice, RegionCode region, AccountType accountType) {
        // 예치금 테이블은 실제로는 지역/면적별 구간으로 관리됨 — 단순화 예시
        if (housePrice <= 200_000_000L) return 200_000L;
        if (housePrice <= 400_000_000L) return 400_000L;
        return 1_000_000L;
    }
    public int reApplyBanMonths() { return 24; }
}

