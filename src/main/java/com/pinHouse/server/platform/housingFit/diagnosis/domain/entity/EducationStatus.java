package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EducationStatus {

    UNIVERSITY_ENROLLED_OR_ADMITTED("대학교 재학 중이거나 다음 학기에 입학 예정"),
    UNIVERSITY_ON_LEAVE("대학교 휴학 중이며 다음 학기 복학 예정"),
    GRADUATED_WITHIN_2Y("대학교 혹은 고등학교 졸업/중퇴 후 2년 이내"),
    GRADUATED_OVER_2Y_GRAD_SCHOOL("졸업/중퇴 후 2년이 지났지만 대학원에 재학 중"),
    NONE("해당 사항 없음");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }
}
