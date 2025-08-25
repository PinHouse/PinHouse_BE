package com.pinHouse.server.platform.domain.diagnosis.rule;

import com.pinHouse.server.platform.domain.diagnosis.entity.Diagnosis;
import com.pinHouse.server.platform.domain.diagnosis.model.EvaluationContext;
import com.pinHouse.server.platform.domain.diagnosis.model.RuleResult;
import com.pinHouse.server.platform.domain.diagnosis.model.Severity;

interface Rule {

    RuleResult evaluate(EvaluationContext ctx);

    String code();

    Severity severity();
}

