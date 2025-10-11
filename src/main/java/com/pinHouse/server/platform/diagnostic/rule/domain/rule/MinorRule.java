package com.pinHouse.server.platform.diagnostic.rule.domain.rule;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.diagnostic.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.diagnostic.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.diagnostic.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Order(6)
@Component
@RequiredArgsConstructor
public class MinorRule implements Rule {

    /// 임대주택 유형 검증기 도입
    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        /// 진단 정보 가져오기
        Diagnosis diagnosis = ctx.getDiagnosis();

        /// 현재 후보 리스트 복사
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        /// 결혼 여부 & 신생아 여부
        boolean minorOk = diagnosis.getUnbornChildrenCount() >= 1;

        /// 신생아가 1명이상 없다면, 제거한다.
        if (!minorOk) {

            /// 삭제
            candidates.removeIf(c ->
                    c.supplyType() == SupplyType.MINOR_SPECIAL);

            /// 결과 저장하기
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "신생아 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", "신생아의 자녀가 없음"
                    ));
        }

        return RuleResult.pass(code(),
                "신생아 특별공급 후보",
                Map.of(
                        "candidate", candidates));
    }


    @Override
    public String code() {
        return "MINOR_SPECIAL";
    }
}
