package com.pinHouse.server.platform.housingFit.rule.domain.entity;

import java.util.List;
import java.util.Map;

public class RentalSupplyMapping {

    public static final Map<RentalType, List<SupplyType>> RENTAL_SUPPLY_MAP = Map.of(
            RentalType.PUBLIC_INTEGRATED, List.of(
                    SupplyType.SPECIAL,         // 미성년자
                    SupplyType.YOUTH_SPECIAL,
                    SupplyType.ELDER_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.GENERAL,
                    SupplyType.MULTICHILD_SPECIAL,
                    SupplyType.DEMOLITION,
                    SupplyType.NATIONAL_MERIT,
                    SupplyType.LONG_SERVICE_VETERAN,
                    SupplyType.NORTH_DEFECTOR,
                    SupplyType.DISABLED,
                    SupplyType.NON_HOUSING_RESIDENT
            ),
            RentalType.NATIONAL_RENTAL, List.of(
                    SupplyType.SPECIAL,         // 미성년자
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.GENERAL,
                    SupplyType.DEMOLITION,
                    SupplyType.UNAUTHORIZED_BUILDING_TENANT
            ),
            RentalType.HAPPY_HOUSING, List.of(
                    SupplyType.STUDENT_SPECIAL,
                    SupplyType.YOUTH_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.ELDER_SPECIAL,
                    SupplyType.DISABLED,
                    SupplyType.NATIONAL_MERIT,
                    SupplyType.PERMANENT_LEASE_EVICTEE,
                    SupplyType.TEMPORARY_STRUCTURE_RESIDENT
            ),
            RentalType.PUBLIC_RENTAL, List.of(
                    SupplyType.GENERAL,
                    SupplyType.MULTICHILD_SPECIAL,
                    SupplyType.ELDER_SUPPORT_SPECIAL,
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.FIRST_SPECIAL,   // 생애최초
                    SupplyType.MINOR_SPECIAL,   // 신생아
                    SupplyType.YOUTH_SPECIAL,   // 청년
                    SupplyType.NATIONAL_MERIT
            ),
            RentalType.PERMANENT_RENTAL, List.of(
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.ELDER_SUPPORT_SPECIAL,
                    SupplyType.ELDER_SPECIAL,
                    SupplyType.NATIONAL_MERIT,
                    SupplyType.NORTH_DEFECTOR,
                    SupplyType.COMFORT_WOMAN_VICTIM,
                    SupplyType.DISABLED
            ),
            RentalType.LONG_TERM_JEONSE, List.of(
                    SupplyType.NEWCOUPLE_SPECIAL,
                    SupplyType.SINGLE_PARENT_SPECIAL,
                    SupplyType.MULTICHILD_SPECIAL
            )
    );

}
