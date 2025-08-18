package com.pinHouse.server.platform.domain.diagnosis.rule;


interface Rule {
    RuleResult evaluate(RuleContext ctx);
    String code();                    // 예: BASE_HOUSEHOLD_HEAD
    Severity severity();              // HARD_FAIL, SOFT_WARN, INFO
}
