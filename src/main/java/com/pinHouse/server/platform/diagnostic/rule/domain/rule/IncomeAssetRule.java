package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.IncomeLevel;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.RuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

/** 10) 가능한 후보에서 소득/자산 한번에 체크 총량 요건 */
@Order(15)
@Component
@RequiredArgsConstructor
public class IncomeAssetRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        /// 진단 정보 가져오기
        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 현재 후보 리스트 복사
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 자동차 자산 금액
        long carValue = diagnosis.getCarValue();

        /// 소득 수준 퍼센트
        IncomeLevel incomeLevel = diagnosis.getIncomeLevel();

        /// 총 자산 금액
        long totalAsset = diagnosis.getTotalAsset();

        /// 가족 수
        int familyCount = diagnosis.getFamilyCount();

        /// Iterator로 후보 순회하며 소득/자산 요건 체크
        var iter = candidates.iterator();
        while (iter.hasNext()) {
            var candidate = iter.next();

            /// 자동차 금액 체크
            boolean carOk = carValue <= policyUseCase.checkMaxCarValue();

            /// 소득 비율 체크
            boolean incomeOk = incomeLevel.getPercent() <=
                    policyUseCase.maxIncomeRatio(candidate.supplyType(), candidate.noticeType(), familyCount);

            /// 총 자산 체크
            boolean assetOk = totalAsset <=
                    policyUseCase.maxTotalAsset(candidate.supplyType(), candidate.noticeType(), familyCount);

            /// 요건 불충족 시 후보 제거
            if (!carOk || !incomeOk || !assetOk) {
                iter.remove();
            }
        }

        /// 최신 후보 리스트 반영
        ctx.setCurrentCandidates(candidates);

        /// 결과 리턴
        return RuleResult.pass(code(),
                "소득/자산 요건 충족 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "INCOME_ASSET_LIMIT";
    }
}
