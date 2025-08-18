package com.pinHouse.server.platform.domain.diagnosis.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
@AllArgsConstructor
public class RuleResult {
    private final boolean pass;       // 통과 여부
    private final String code;        // 규칙 코드
    private final Severity severity;  // 심각도
    private final String message;     // 사용자 메시지
    private final Map<String, Object> details; // 디버그/설명용 세부값

    static RuleResult pass(String code, Severity severity, String msg, Map<String,Object> details) {
        return new RuleResult(true, code, severity, msg, details == null ? Collections.emptyMap() : details);
    }

    static RuleResult fail(String code, Severity severity, String msg, Map<String,Object> details) {
        return new RuleResult(false, code, severity, msg, details == null ? Collections.emptyMap() : details);
    }
}
