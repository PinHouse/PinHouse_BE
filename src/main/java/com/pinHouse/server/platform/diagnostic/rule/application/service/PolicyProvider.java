package com.pinHouse.server.platform.diagnostic.rule.application.service;

import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeType;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * SupplyType + RentalType 세트를 모두 반영한 정책 제공
 */
@Component
public class PolicyProvider implements PolicyUseCase {

    /**
     * 최대 소득 비율 (%) - 가구원수 반영
     * 기준: 전년도 도시근로자 가구원수별 월평균 소득 대비 비율 또는 기준중위소득 대비 비율
     */
    @Override
    public double maxIncomeRatio(SupplyType supply, NoticeType rental, int familyCount) {
        return switch (rental) {

            /// 통합공공임대 (우선공급: 100%, 일반공급: 150%)
            case PUBLIC_INTEGRATED -> switch (supply) {
                /// 청년 특별공급 - 가구원 수별 차등 (1인 170%, 2인 160%, 3인 이상 150%)
                case YOUTH_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0), 150.0);

                /// 고령자 특별공급 - 가구원 수별 차등 (1인 170%, 2인 160%, 3인 이상 150%)
                case ELDER_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0), 150.0);

                /// 신혼부부 특별공급 - 가구원 수별 차등 + 맞벌이 가구 가산
                /// (1인 170%, 2인 160%, 3인 150%, 맞벌이 2인 190%, 맞벌이 3인 이상 180%)
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0, 100, 190.0, 101, 180.0), 150.0);

                /// 기타 우선공급 (다자녀, 특별계층 등) - 가구원 수별 차등
                case SPECIAL, MULTICHILD_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 100.0), 100.0);

                /// 나머지 특별공급 - 기준중위소득 기본
                case SINGLE_PARENT_SPECIAL, MINOR_SPECIAL, FIRST_SPECIAL, ELDER_SUPPORT_SPECIAL,
                     NATIONAL_MERIT, DEMOLITION, LONG_SERVICE_VETERAN, NORTH_DEFECTOR,
                     DISABLED, NON_HOUSING_RESIDENT -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0), 150.0);

                /// 일반공급 - 기준중위소득 150%
                case GENERAL -> 150.0;

                default -> 150.0;
            };

            /// 영구임대 (전년도 도시근로자 월평균 소득 50~100%)
            case PERMANENT_RENTAL -> switch (supply) {
                /// 일반 - 50% 이하
                case GENERAL -> 50.0;

                /// 국가유공자, 북한이탈주민, 아동복지시설퇴소자, 장애인(1순위) - 70% 이하
                case NATIONAL_MERIT, NORTH_DEFECTOR, DISABLED -> 70.0;

                /// 장애인(2순위) - 100% 이하
                default -> 100.0;
            };

            /// 국민임대 (60㎡ 이하: 70%, 60㎡ 초과: 100%)
            case NATIONAL_RENTAL -> switch (supply) {
                /// 60㎡ 이하 기준
                case GENERAL, MULTICHILD_SPECIAL, NATIONAL_MERIT, NEWCOUPLE_SPECIAL,
                     DEMOLITION, NON_HOUSING_RESIDENT, DISABLED, ELDER_SPECIAL,
                     ELDER_SUPPORT_SPECIAL, PERMANENT_LEASE_EVICTEE -> 70.0;

                /// 60㎡ 초과는 100%로 처리 (면적 정보 없으면 보수적으로 70% 적용)
                default -> 70.0;
            };

            /// 장기전세 (60㎡ 이하: 100%, 60㎡ 초과: 120%)
            case LONG_TERM_JEONSE -> switch (supply) {
                /// 60㎡ 이하 기준
                case GENERAL, MULTICHILD_SPECIAL, NATIONAL_MERIT, NEWCOUPLE_SPECIAL,
                     DEMOLITION, NON_HOUSING_RESIDENT, DISABLED, ELDER_SPECIAL,
                     ELDER_SUPPORT_SPECIAL, PERMANENT_LEASE_EVICTEE -> 100.0;

                /// 60㎡ 초과는 120%로 처리
                default -> 100.0;
            };

            /// 공공임대 (신혼/생애최초/다자녀/노부모 일반 60㎡ 이하만 적용)
            case PUBLIC_RENTAL -> switch (supply) {
                /// 대학생, 청년, 고령자 특별공급 - 기준중위소득 100%
                case STUDENT_SPECIAL, YOUTH_SPECIAL, ELDER_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0), 100.0);

                /// 다자녀, 노부모 특별공급 - 기준중위소득 120%
                case ELDER_SUPPORT_SPECIAL, MULTICHILD_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 120.0), 120.0);

                /// 신혼부부 특별공급 - 기준중위소득 130% (맞벌이 140%)
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 130.0, 100, 140.0), 130.0);

                /// 일반공급, 생애최초 등 기본 - 기준중위소득 100~130%
                default -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 100.0), 100.0);
            };

            /// 행복주택 (계층별 소득 기준 상이)
            case HAPPY_HOUSING -> switch (supply) {
                /// 대학생, 청년, 고령자 특별공급 - 기준중위소득 100%
                case STUDENT_SPECIAL, YOUTH_SPECIAL, ELDER_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0), 100.0);

                /// 신혼부부 특별공급 - 기본 100%, 맞벌이 120%
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0, 100, 120.0), 100.0);

                /// 기타 - 가구원 수별 차등
                default -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 70.0), 70.0);
            };

            default -> 150.0;
        };
    }

    /**
     * familyCount별 고정 표가 있으면 그 값, 없으면 유연하게 fallback
     */
    private double incomeByFamily(int familyCount, Map<Integer, Double> table, double defaultValue) {
        if (table.containsKey(familyCount)) {
            return table.get(familyCount);
        }
        // 유연 처리: 표에 없는 경우 → 가장 큰 key 초과면 defaultValue, 그 외엔 가장 가까운 값 선택
        int maxKey = table.keySet().stream().mapToInt(i -> i).max().orElse(0);
        int minKey = table.keySet().stream().mapToInt(i -> i).min().orElse(0);

        if (familyCount > maxKey) {
            return defaultValue; // 초과 시 fallback
        } else if (familyCount < minKey) {
            return table.get(minKey); // 최소값보다 작으면 최소값으로
        } else {
            // 중간 값인데 정의 안 된 경우 → 가장 가까운 key 찾기
            return table.entrySet().stream()
                    .min((e1, e2) ->
                            Integer.compare(
                                    Math.abs(e1.getKey() - familyCount),
                                    Math.abs(e2.getKey() - familyCount)))
                    .map(Map.Entry::getValue)
                    .orElse(defaultValue);
        }
    }


    /**
     * 최대 총자산 제한 (단위: 원)
     * 통합공공임대/국민임대/행복주택: 총자산 33,700만원 이하
     * 영구임대: 총자산 23,700만원 이하
     * 장기전세/공공임대: 부동산 21,550만원 이하
     */
    @Override
    public long maxTotalAsset(SupplyType supply, NoticeType rental, int familyCount) {
        return switch (rental) {
            /// 통합공공임대 - 총자산 337,000,000원 (우선공급 중 일부 적용 제외)
            case PUBLIC_INTEGRATED -> 337_000_000L;

            /// 영구임대 - 총자산 237,000,000원 (수급자 등 일부 대상 제외)
            case PERMANENT_RENTAL -> 237_000_000L;

            /// 국민임대 - 총자산 337,000,000원 (우선공급 중 일부 적용 제외)
            case NATIONAL_RENTAL -> 337_000_000L;

            /// 장기전세 - 부동산 215,500,000원 (우선공급 중 일부 적용 제외)
            case LONG_TERM_JEONSE -> 215_500_000L;

            /// 공공임대 - 부동산 215,500,000원 (기타특별 제외)
            case PUBLIC_RENTAL -> 215_500_000L;

            /// 행복주택 - 계층별 총자산 기준 상이
            case HAPPY_HOUSING -> switch (supply) {
                /// 대학생 특별공급 - 총자산 104,000,000원 (1억 400만원)
                case STUDENT_SPECIAL -> 104_000_000L;

                /// 청년 특별공급 - 총자산 254,000,000원 (2억 5천 4백만원) - 맞벌이 등 고려
                case YOUTH_SPECIAL -> 254_000_000L;

                /// 기타 - 총자산 337,000,000원 (3억 3천 7백만원)
                default -> 337_000_000L;
            };

            /// 기타 임대 유형 기본값
            default -> 337_000_000L;
        };
    }


    /**
     * 자동차 자산 제한 - 45,630,000원 (4,563만원)
     * 모든 임대주택 유형 공통 적용
     */
    @Override
    public long checkMaxCarValue() {
        return 45_630_000L;
    }


    @Override
    public int youthAgeMin() {
        return 19;
    }

    @Override
    public int newlyMarriedMaxYears() {
        return 7;
    }

    @Override
    public int marriedYouthAgeMin() {
        return 20;
    }

    @Override
    public int elderAge() {
        return 65;
    }

    @Override
    public int reApplyBanMonths(NoticeType rental) {
        return 24;
    }
}
