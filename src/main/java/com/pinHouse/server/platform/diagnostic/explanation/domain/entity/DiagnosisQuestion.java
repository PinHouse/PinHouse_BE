package com.pinHouse.server.platform.diagnostic.explanation.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DiagnosisQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private DiagnosisType diagnosisType;

    private int orderNo;

    /// 정적 팩토리 메서드
    public static DiagnosisQuestion of(String questionText, DiagnosisType diagnosisType) {
        return DiagnosisQuestion.builder()
                .questionText(questionText)
                .diagnosisType(diagnosisType)
                .orderNo(0)
                .build();
    }

}
