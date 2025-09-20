package com.pinHouse.server.platform.housingFit.rule.domain.rule;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.EducationStatus;
import com.pinHouse.server.platform.housingFit.rule.application.dto.response.RuleResult;
import com.pinHouse.server.platform.housingFit.rule.application.usecase.PolicyUseCase;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.EvaluationContext;
import com.pinHouse.server.platform.housingFit.rule.domain.entity.SupplyType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Order(11)
@Component
@RequiredArgsConstructor
public class StudentRule implements Rule {

    private final PolicyUseCase policyUseCase;

    @Override
    public RuleResult evaluate(EvaluationContext ctx) {

        Diagnosis diagnosis = ctx.getDiagnosis();
        var candidates = new ArrayList<>(ctx.getCurrentCandidates());

        boolean isStudent = !diagnosis.getEducationStatus().equals(EducationStatus.NONE);
        boolean isMarried = diagnosis.isMaritalStatus();
        boolean hasCar = diagnosis.getCarValue() > 0 || diagnosis.isHasCar();

        // 기본적으로 실패 이유를 수집
        var failReasons = new ArrayList<String>();

        if (!isStudent) {
            failReasons.add("학생이 아님");
        }
        if (isMarried) {
            failReasons.add("기혼 상태");
        }
        if (hasCar) {
            failReasons.add("차량 보유");
        }

        // 조건 불충족 -> 후보에서 제거
        if (!failReasons.isEmpty()) {
            candidates.removeIf(c -> c.supplyType() == SupplyType.STUDENT_SPECIAL);
            ctx.setCurrentCandidates(candidates);

            return RuleResult.fail(code(),
                    "대학생 특별공급 해당 없음",
                    Map.of(
                            "candidate", candidates,
                            "failReason", failReasons
                    ));
        }

        // 모든 조건 만족
        return RuleResult.pass(code(),
                "대학생 특별공급 후보",
                Map.of("candidate", candidates));
    }

    @Override
    public String code() {
        return "CANDIDATE_STUDENT_SPECIAL";
    }
}

