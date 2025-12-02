package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.*;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyRentalCandidate;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 임대주택 진단 로직 통합 테스트
 * 실제 케이스를 기반으로 각 규칙의 동작을 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("임대주택 진단 규칙 통합 테스트")
class RentalHousingRuleIntegrationTest {

    @Mock
    private PolicyUseCase policyUseCase;

    private BaseEligibilityRule baseEligibilityRule;
    private GeneralSupplyRule generalSupplyRule;
    private YouthCandidateRule youthCandidateRule;
    private NewlyMarriedCandidateRule newlyMarriedCandidateRule;
    private FirstSpecialRule firstSpecialRule;
    private MultiChildCandidateRule multiChildCandidateRule;
    private ElderSupportCandidateRule elderSupportCandidateRule;

    @BeforeEach
    void setUp() {
        // PolicyUseCase Mock 설정 (lenient 모드로 불필요한 stubbing 경고 제거)
        org.mockito.Mockito.lenient().when(policyUseCase.youthAgeMin()).thenReturn(19);
        org.mockito.Mockito.lenient().when(policyUseCase.newlyMarriedMaxYears()).thenReturn(7);
        org.mockito.Mockito.lenient().when(policyUseCase.elderAge()).thenReturn(65);
        org.mockito.Mockito.lenient().when(policyUseCase.checkMaxCarValue()).thenReturn(36_000_000L);
        org.mockito.Mockito.lenient().when(policyUseCase.maxIncomeRatio(any(), any(), anyInt())).thenReturn(100.0);
        org.mockito.Mockito.lenient().when(policyUseCase.maxTotalAsset(any(), any(), anyInt())).thenReturn(300_000_000L);

        // Rule 인스턴스 생성
        baseEligibilityRule = new BaseEligibilityRule(policyUseCase);
        generalSupplyRule = new GeneralSupplyRule(policyUseCase);
        youthCandidateRule = new YouthCandidateRule(policyUseCase);
        newlyMarriedCandidateRule = new NewlyMarriedCandidateRule(policyUseCase);
        firstSpecialRule = new FirstSpecialRule(policyUseCase);
        multiChildCandidateRule = new MultiChildCandidateRule(policyUseCase);
        elderSupportCandidateRule = new ElderSupportCandidateRule(policyUseCase);
    }

    /**
     * 테스트 케이스 1: 청년 특별공급 적격자
     * - 나이: 28세
     * - 무주택 세대주
     * - 소득: 중위소득 100% 이하
     *
     * 예측: 청년 특별공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 1: 28세 무주택 세대주 - 청년 특별공급 적격")
    void testCase1_YouthSpecialSupply_Qualified() {
        // Given: 28세, 무주택 세대주, 1인 가구
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(28)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(true)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(false)
                .incomeLevel(IncomeLevel.PERCENT_100)
                .adultCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        RuleResult baseResult = baseEligibilityRule.evaluate(ctx);

        // Then: 기초 자격 통과 확인
        assertThat(baseResult.pass()).isTrue();
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.YOUTH_SPECIAL)
                .doesNotContain(SupplyType.ELDER_SPECIAL); // 28세는 고령자 특별공급 제외

        // When: 청년 특별공급 규칙 적용
        RuleResult youthResult = youthCandidateRule.evaluate(ctx);

        // Then: 청년 특별공급 후보로 선정
        assertThat(youthResult.pass()).isTrue();
        assertThat(youthResult.message()).isEqualTo("청년 특별공급 후보");

        System.out.println("=== 케이스 1: 청년 특별공급 적격자 ===");
        System.out.println("나이: 28세 | 세대주: O | 주택: 무주택");
        System.out.println("예측: 청년 특별공급 적격");
        System.out.println("실제: " + (youthResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * 테스트 케이스 2: 청년 특별공급 부적격 (나이 초과)
     * - 나이: 40세
     * - 무주택 세대주
     *
     * 예측: 청년 특별공급 제외
     * 실제: FAIL
     */
    @Test
    @DisplayName("케이스 2: 40세 무주택 세대주 - 청년 특별공급 부적격 (나이 초과)")
    void testCase2_YouthSpecialSupply_AgeExceeded() {
        // Given: 40세, 무주택 세대주
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(40)
                .gender(Gender.Female)
                .isHouseholdHead(true)
                .isSingle(true)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .adultCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // Then: 청년 특별공급이 기초 자격에서 제거됨 (40세는 청년 범위 초과)
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .doesNotContain(SupplyType.YOUTH_SPECIAL);

        // When: 청년 특별공급 규칙 적용
        RuleResult youthResult = youthCandidateRule.evaluate(ctx);

        // Then: 이미 후보에서 제외되어 있음
        assertThat(youthResult.pass()).isFalse();
        assertThat(youthResult.message()).isEqualTo("청년 특별공급 해당 없음");

        System.out.println("=== 케이스 2: 청년 특별공급 부적격 (나이 초과) ===");
        System.out.println("나이: 40세 | 세대주: O | 주택: 무주택");
        System.out.println("예측: 청년 특별공급 부적격 (나이 초과)");
        System.out.println("실제: " + (youthResult.pass() ? "PASS ✓" : "FAIL ✗ - " + youthResult.details().get("failReason")));
        System.out.println();
    }

    /**
     * 테스트 케이스 3: 신혼부부 특별공급 적격자
     * - 나이: 32세
     * - 결혼 3년차
     * - 무주택 세대주
     * - 6세 이하 자녀 1명
     *
     * 예측: 신혼부부 특별공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 3: 결혼 3년차, 6세 이하 자녀 1명 - 신혼부부 특별공급 적격")
    void testCase3_NewlyMarriedSpecialSupply_Qualified() {
        // Given: 32세, 결혼 3년차, 6세 이하 자녀 1명
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(32)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(false)
                .maritalStatus(true)
                .marriageYears(3)
                .under6ChildrenCount(1)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.ONE_TO_TWO_YEARS)
                .adultCount(2)
                .minorCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 신혼부부 특별공급 규칙 적용
        RuleResult newlyMarriedResult = newlyMarriedCandidateRule.evaluate(ctx);

        // Then: 신혼부부 특별공급 후보로 선정
        assertThat(newlyMarriedResult.pass()).isTrue();
        assertThat(newlyMarriedResult.message()).isEqualTo("신혼부부 해당 조건 충족");
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.NEWCOUPLE_SPECIAL);

        System.out.println("=== 케이스 3: 신혼부부 특별공급 적격자 ===");
        System.out.println("나이: 32세 | 결혼: 3년차 | 자녀: 6세 이하 1명 | 주택: 무주택");
        System.out.println("예측: 신혼부부 특별공급 적격");
        System.out.println("실제: " + (newlyMarriedResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * 테스트 케이스 4: 신혼부부 특별공급 부적격 (결혼 기간 초과, 자녀 없음)
     * - 나이: 35세
     * - 결혼 8년차
     * - 무주택 세대주
     * - 자녀 없음
     *
     * 예측: 신혼부부 특별공급 제외
     * 실제: FAIL
     */
    @Test
    @DisplayName("케이스 4: 결혼 8년차, 자녀 없음 - 신혼부부 특별공급 부적격")
    void testCase4_NewlyMarriedSpecialSupply_MarriageYearsExceeded() {
        // Given: 35세, 결혼 8년차, 자녀 없음
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(35)
                .gender(Gender.Female)
                .isHouseholdHead(true)
                .isSingle(false)
                .maritalStatus(true)
                .marriageYears(8)
                .under6ChildrenCount(0)
                .over7MinorChildrenCount(0)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .adultCount(2)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 신혼부부 특별공급 규칙 적용
        RuleResult newlyMarriedResult = newlyMarriedCandidateRule.evaluate(ctx);

        // Then: 신혼부부 특별공급 제외
        assertThat(newlyMarriedResult.pass()).isFalse();
        assertThat(newlyMarriedResult.message()).isEqualTo("신혼부부 조건 해당 없음");
        assertThat(newlyMarriedResult.details().get("failReason"))
                .isEqualTo("결혼 7년 초과 및 자녀 요건 미충족");

        System.out.println("=== 케이스 4: 신혼부부 특별공급 부적격 ===");
        System.out.println("나이: 35세 | 결혼: 8년차 | 자녀: 없음 | 주택: 무주택");
        System.out.println("예측: 신혼부부 특별공급 부적격 (결혼 7년 초과, 자녀 없음)");
        System.out.println("실제: " + (newlyMarriedResult.pass() ? "PASS ✓" : "FAIL ✗ - " + newlyMarriedResult.details().get("failReason")));
        System.out.println();
    }

    /**
     * 테스트 케이스 5: 다자녀 특별공급 적격자
     * - 나이: 38세
     * - 자녀 3명 (태아 1명, 6세 이하 1명, 미성년자 1명)
     * - 무주택 세대주
     *
     * 예측: 다자녀 특별공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 5: 자녀 3명 (태아 포함) - 다자녀 특별공급 적격")
    void testCase5_MultiChildSpecialSupply_Qualified() {
        // Given: 38세, 자녀 3명
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(38)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(false)
                .maritalStatus(true)
                .marriageYears(10)
                .unbornChildrenCount(1)  // 태아 1명
                .under6ChildrenCount(1)  // 6세 이하 1명
                .over7MinorChildrenCount(1)  // 7세 이상 미성년자 1명
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .adultCount(2)
                .minorCount(2)
                .fetusCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 다자녀 특별공급 규칙 적용
        RuleResult multiChildResult = multiChildCandidateRule.evaluate(ctx);

        // Then: 다자녀 특별공급 후보로 선정
        assertThat(multiChildResult.pass()).isTrue();
        assertThat(multiChildResult.message()).isEqualTo("다자녀 특별공급 후보");
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.MULTICHILD_SPECIAL);

        System.out.println("=== 케이스 5: 다자녀 특별공급 적격자 ===");
        System.out.println("나이: 38세 | 자녀: 3명 (태아 1명, 6세 이하 1명, 미성년자 1명) | 주택: 무주택");
        System.out.println("예측: 다자녀 특별공급 적격");
        System.out.println("실제: " + (multiChildResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * 테스트 케이스 6: 일반공급 적격자
     * - 나이: 42세
     * - 무주택 세대주
     * - 청약통장 2년 이상 가입
     *
     * 예측: 일반공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 6: 무주택 세대주, 청약통장 2년 가입 - 일반공급 적격")
    void testCase6_GeneralSupply_Qualified() {
        // Given: 42세, 무주택 세대주, 청약통장 2년 이상
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(42)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(false)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .account(SubscriptionAccount.UNDER_600)
                .accountDeposit(SubscriptionCount.OVER_24)
                .adultCount(2)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 일반공급 규칙 적용
        RuleResult generalResult = generalSupplyRule.evaluate(ctx);

        // Then: 일반공급 후보로 선정
        assertThat(generalResult.pass()).isTrue();
        assertThat(generalResult.message()).isEqualTo("일반공급 후보");
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.GENERAL);

        System.out.println("=== 케이스 6: 일반공급 적격자 ===");
        System.out.println("나이: 42세 | 세대주: O | 주택: 무주택 | 청약통장: 2년 이상");
        System.out.println("예측: 일반공급 적격");
        System.out.println("실제: " + (generalResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * 테스트 케이스 7: 일반공급 부적격 (청약통장 가입기간 부족)
     * - 나이: 35세
     * - 무주택 세대주
     * - 청약통장 3개월 가입
     *
     * 예측: 일반공급 제외
     * 실제: FAIL
     */
    @Test
    @DisplayName("케이스 7: 청약통장 가입 3개월 - 일반공급 부적격 (가입기간 부족)")
    void testCase7_GeneralSupply_InsufficientAccountPeriod() {
        // Given: 35세, 무주택 세대주, 청약통장 3개월
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(35)
                .gender(Gender.Female)
                .isHouseholdHead(true)
                .isSingle(true)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.LESS_THAN_6_MONTHS)  // 6개월 미만
                .adultCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 일반공급 규칙 적용
        RuleResult generalResult = generalSupplyRule.evaluate(ctx);

        // Then: 일반공급 부적격
        assertThat(generalResult.pass()).isFalse();
        assertThat(generalResult.message()).isEqualTo("일반공급 해당 없음");
        assertThat(generalResult.details().get("failReason"))
                .isEqualTo("청약통장 가입기간 부족 (최소 6개월 필요)");

        System.out.println("=== 케이스 7: 일반공급 부적격 (가입기간 부족) ===");
        System.out.println("나이: 35세 | 세대주: O | 주택: 무주택 | 청약통장: 3개월");
        System.out.println("예측: 일반공급 부적격 (가입기간 부족)");
        System.out.println("실제: " + (generalResult.pass() ? "PASS ✓" : "FAIL ✗ - " + generalResult.details().get("failReason")));
        System.out.println();
    }

    /**
     * 테스트 케이스 8: 생애최초 특별공급 적격자
     * - 나이: 36세
     * - 무주택 세대주
     * - 결혼 5년차
     * - 자녀 1명
     *
     * 예측: 생애최초 특별공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 8: 무주택 세대주, 결혼 5년차, 자녀 1명 - 생애최초 특별공급 적격")
    void testCase8_FirstSpecialSupply_Qualified() {
        // Given: 36세, 무주택 세대주, 결혼 5년차, 자녀 1명
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(36)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(false)
                .maritalStatus(true)
                .marriageYears(5)
                .under6ChildrenCount(1)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .adultCount(2)
                .minorCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 생애최초 특별공급 규칙 적용
        RuleResult firstSpecialResult = firstSpecialRule.evaluate(ctx);

        // Then: 생애최초 특별공급 후보로 선정
        assertThat(firstSpecialResult.pass()).isTrue();
        assertThat(firstSpecialResult.message()).isEqualTo("생애최초 특별공급 후보");
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.FIRST_SPECIAL);

        System.out.println("=== 케이스 8: 생애최초 특별공급 적격자 ===");
        System.out.println("나이: 36세 | 세대주: O | 주택: 무주택 | 결혼: 5년차 | 자녀: 1명");
        System.out.println("예측: 생애최초 특별공급 적격");
        System.out.println("실제: " + (firstSpecialResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * 테스트 케이스 9: 주택 소유자 - 모든 공급 부적격
     * - 나이: 30세
     * - 주택 소유
     *
     * 예측: 모든 임대주택 공급 부적격
     * 실제: FAIL
     */
    @Test
    @DisplayName("케이스 9: 주택 소유자 - 모든 공급 부적격")
    void testCase9_HouseOwner_AllSupplyDisqualified() {
        // Given: 30세, 주택 소유
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(30)
                .gender(Gender.Male)
                .isHouseholdHead(true)
                .isSingle(true)
                .housingStatus(HousingOwnershipStatus.OWN_HOUSE)  // 주택 소유
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .adultCount(1)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        RuleResult baseResult = baseEligibilityRule.evaluate(ctx);

        // Then: 기초 자격 통과 실패 (주택 소유자)
        assertThat(baseResult.pass()).isFalse();
        assertThat(baseResult.message()).isEqualTo("주택 소유자는 임대주택 지원이 불가능");

        System.out.println("=== 케이스 9: 주택 소유자 - 모든 공급 부적격 ===");
        System.out.println("나이: 30세 | 주택: 소유");
        System.out.println("예측: 모든 임대주택 공급 부적격");
        System.out.println("실제: " + (baseResult.pass() ? "PASS ✓" : "FAIL ✗ - " + baseResult.message()));
        System.out.println();
    }

    /**
     * 테스트 케이스 10: 노부모 부양 특별공급 적격자
     * - 나이: 45세
     * - 무주택 세대주
     * - 노부모 부양 특별계층
     *
     * 예측: 노부모 부양 특별공급 후보로 선정
     * 실제: PASS
     */
    @Test
    @DisplayName("케이스 10: 노부모 부양 - 노부모 부양 특별공급 적격")
    void testCase10_ElderSupportSpecialSupply_Qualified() {
        // Given: 45세, 무주택 세대주, 노부모 부양
        Diagnosis diagnosis = createDiagnosisBuilder()
                .age(45)
                .gender(Gender.Female)
                .isHouseholdHead(true)
                .isSingle(false)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .hasAccount(true)
                .accountYears(SubscriptionPeriod.OVER_TWO_YEARS)
                .hasSpecialCategory(List.of(SpecialCategory.SUPPORTING_ELDERLY))  // 노부모 부양
                .adultCount(3)
                .build();

        EvaluationContext ctx = EvaluationContext.of(diagnosis);

        // When: 기초 자격 규칙 적용
        baseEligibilityRule.evaluate(ctx);

        // When: 노부모 부양 특별공급 규칙 적용
        RuleResult elderSupportResult = elderSupportCandidateRule.evaluate(ctx);

        // Then: 노부모 부양 특별공급 후보로 선정
        assertThat(elderSupportResult.pass()).isTrue();
        assertThat(elderSupportResult.message()).isEqualTo("노부모 부양 특별공급 후보");
        assertThat(ctx.getCurrentCandidates())
                .extracting(SupplyRentalCandidate::supplyType)
                .contains(SupplyType.ELDER_SUPPORT_SPECIAL);

        System.out.println("=== 케이스 10: 노부모 부양 특별공급 적격자 ===");
        System.out.println("나이: 45세 | 세대주: O | 주택: 무주택 | 특별계층: 노부모 부양");
        System.out.println("예측: 노부모 부양 특별공급 적격");
        System.out.println("실제: " + (elderSupportResult.pass() ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }

    /**
     * Diagnosis 빌더 생성 헬퍼 메서드
     */
    private Diagnosis.DiagnosisBuilder createDiagnosisBuilder() {
        return Diagnosis.builder()
                .gender(Gender.Male)
                .age(30)
                .monthPay(0)
                .hasAccount(false)
                .accountYears(null)
                .accountDeposit(null)
                .account(null)
                .maritalStatus(false)
                .marriageYears(null)
                .unbornChildrenCount(0)
                .under6ChildrenCount(0)
                .over7MinorChildrenCount(0)
                .educationStatus(null)
                .hasCar(false)
                .carValue(0)
                .isHouseholdHead(false)
                .isSingle(true)
                .fetusCount(0)
                .minorCount(0)
                .adultCount(1)
                .incomeLevel(IncomeLevel.PERCENT_100)
                .housingStatus(HousingOwnershipStatus.NO_ONE_OWNS_HOUSE)
                .housingYears(0)
                .propertyAsset(0)
                .carAsset(0)
                .financialAsset(0)
                .hasSpecialCategory(new ArrayList<>());
    }
}
