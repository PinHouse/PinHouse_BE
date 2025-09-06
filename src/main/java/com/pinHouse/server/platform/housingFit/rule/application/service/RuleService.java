package com.pinHouse.server.platform.housingFit.rule.application.service;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.usecase.RuleChainUseCase;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.domain.rule.Rule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 룰체인 실행
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RuleService implements RuleChainUseCase {

    /// 실행할 룰 목록 조회
    private final List<Rule> rules;

    /**
     * 진단 요청 내용을 바탕으로 룰 실행
     * @param diagnosis     진단 엔티티
     */
    public EvaluationContext evaluateAll(Diagnosis diagnosis) {

        /// EvaluationContext 생성
        EvaluationContext context = EvaluationContext.of(diagnosis);

        /// 반복해서 룰 실행시키기
        for (Rule rule : rules) {

            /// 진행한 룰 결과 더하기
            RuleResult result = rule.evaluate(context);
            context.addResult(result);
        }

        return context;
    }

}
