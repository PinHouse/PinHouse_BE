package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import java.util.List;
import java.util.Map;

public class RentalSupplyMapping {

    public static final Map<RentalType, List<SupplyType>> RENTAL_SUPPLY_MAP = Map.of(
            RentalType.PUBLIC_INTEGRATED, List.of(
                    SupplyType.YOUTH_SPECIAL,
                    SupplyType.ELDER_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.GENERAL,
                    SupplyType.MULTICHILD_SPECIAL,
                    SupplyType.SPECIAL
            ),
            RentalType.NATIONAL_RENTAL, List.of(
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.GENERAL,
                    SupplyType.SPECIAL
            ),
            RentalType.HAPPY_HOUSING, List.of(
                    SupplyType.STUDENT_SPECIAL,
                    SupplyType.YOUTH_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.ELDER_SPECIAL
            ),
            RentalType.PUBLIC_RENTAL, List.of(
                    SupplyType.GENERAL,
                    SupplyType.MULTICHILD_SPECIAL,
                    SupplyType.ELDER_SUPPORT_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.FIRST_SPECIAL,   // 생애최초
                    SupplyType.MINOR_SPECIAL,   // 신생아
                    SupplyType.YOUTH_SPECIAL,
                    SupplyType.SPECIAL
            ),
            RentalType.PERMANENT_RENTAL, List.of(
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.ELDER_SUPPORT_SPECIAL,
                    SupplyType.ELDER_SPECIAL,
                    SupplyType.SPECIAL
            ),
            RentalType.LONG_TERM_JEONSE, List.of(
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.MULTICHILD_SPECIAL
            )
    );
}
