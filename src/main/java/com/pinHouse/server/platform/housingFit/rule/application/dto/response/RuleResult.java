package com.pinHouse.server.platform.housingFit.rule.application.dto.response;

import lombok.Builder;

import java.util.Collections;
import java.util.Map;

/**
 * @param pass     통과 여부
 * @param code     규칙 코드
 * @param message  사용자 메시지
 * @param details  디버그/설명용 세부값
 */
@Builder
public record RuleResult(
        boolean pass,
        String code,
        String message,
        Map<String, Object> details
) {

    /// 정적 팩토리 메서드
    public static RuleResult pass(String code, String msg, Map<String, Object> details) {
        return new RuleResult(true, code, msg, details == null ? Collections.emptyMap() : details);
    }

    /// 정적 팩토리 메서드
    public static RuleResult fail(String code, String msg, Map<String, Object> details) {
        return new RuleResult(false, code, msg, details == null ? Collections.emptyMap() : details);
    }
}
