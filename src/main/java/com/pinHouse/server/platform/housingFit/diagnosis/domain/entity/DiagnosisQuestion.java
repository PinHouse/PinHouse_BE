package com.pinHouse.server.platform.housingFit.diagnosis.domain.entity;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DiagnosisQuestion {

    private Long id;

    private String questionText;

    private DiagnosisType diagnosisType;

    private int orderNo;


    /// 정적 팩토리 메서드
    public static DiagnosisQuestion of(String questionText, DiagnosisType diagnosisType) {
        return DiagnosisQuestion.builder()
                .questionText(questionText)
                .diagnosisType(diagnosisType)
                .build();
    }

}
