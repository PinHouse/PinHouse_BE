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
     * 최대 소득 비율 (%) -가구원수 반영
     */
    @Override
    public double maxIncomeRatio(SupplyType supply, NoticeType rental, int familyCount) {
        return switch (rental) {

            /// 통합공공임대 경우
            case PUBLIC_INTEGRATED -> switch (supply) {
                /// 국가유공자, 장기복무 제대군인, 북한이탈주민, 다자녀가구, 장애인, 비주택거주자, 청년, 신혼부부·한부모가족,고령자
                case SPECIAL, MULTICHILD_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 100.0), 100.0);

                /// 청년, 고령자
                case YOUTH_SPECIAL, ELDER_SPECIAL-> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0), 150.0);

                /// 신혼부부·한부모가족는 맞벌이하느 2인(100, 190), 3인 이상(100,180)까지 증가
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0, 100, 190.0, 101, 180.0), 150.0);

                default -> incomeByFamily(familyCount,
                        Map.of(1, 170.0, 2, 160.0, 3, 150.0), 150.0);
            };

            /// 국민임대
            case NATIONAL_RENTAL -> /// 기본은 1인 90, 2인 80, 그 이상 70
                    incomeByFamily(familyCount,
                            Map.of(1, 90.0, 2, 80.0, 3, 70.0), 70.0);

            /// 행복주택
            case HAPPY_HOUSING -> switch (supply) {
                /// 대학생, 청년, 고령자는 100 이하
                case STUDENT_SPECIAL, YOUTH_SPECIAL, ELDER_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0), 100.0);

                /// 맞벌이하는 부부는 120까지 가능
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0, 100, 120.0), 100.0);

                /// 기본은
                default -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 70.0), 70.0);
            };

            /// 공공임대
            case PUBLIC_RENTAL -> switch (supply) {
                /// 대학생, 청년, 고령자는 100 이하
                case STUDENT_SPECIAL, YOUTH_SPECIAL, ELDER_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 100.0), 100.0);

                case ELDER_SUPPORT_SPECIAL, MULTICHILD_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 120.0), 120.0);

                /// 맞벌이하는 부부는 120까지 가능
                case NEWCOUPLE_SPECIAL -> incomeByFamily(familyCount,
                        Map.of(1, 130.0, 100, 140.0), 130);

                /// 기본은
                default -> incomeByFamily(familyCount,
                        Map.of(1, 120.0, 2, 110.0, 3, 100.0), 100);
            };
            /// 장기전세
            case LONG_TERM_JEONSE -> incomeByFamily(familyCount,
                    Map.of(1, 110.0, 2, 115.0, 3, 120.0), 130.0);

            /// 영구임대
            case PERMANENT_RENTAL -> incomeByFamily(familyCount,
                    Map.of(1, 60.0, 2, 65.0, 3, 70.0), 80.0);

            /// 장기전세

            default -> incomeByFamily(familyCount,
                    Map.of(1, 140.0, 2, 145.0, 3, 150.0), 160.0);
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
     * 최대 금융자산 제한, 3억 3천 7백만원
     */
    @Override
    public long maxTotalAsset(SupplyType supply, NoticeType rental, int familyCount) {
        return switch (rental) {
            case HAPPY_HOUSING -> switch (supply) {
                // 대학생, 청년, 고령자는 1억 400만 이하
                case STUDENT_SPECIAL -> 104_000_000L;

                // 맞벌이 청년은 2억 5천 4백까지 가능
                case YOUTH_SPECIAL -> 254_000_000L;

                // 기본은 3억 3천 7백
                default -> 337_000_000L;
            };
            // HAPPY_HOUSING 이 아닌 경우는 기본값
            default -> 337_000_000L;
        };
    }


    /**
     * 자동차 자산 제한, 대학생은 소유 자체 불가능
     */
    @Override
    public long checkMaxCarValue() {
        return 38300000L;
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
