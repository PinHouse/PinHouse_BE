package com.pinHouse.server.platform.adapter.out.jpa.diagnosis;

import com.pinHouse.server.platform.housingFit.diagnosis.domain.entity.DiagnosisType;
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
public class DiagnosisQuestionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private DiagnosisType diagnosisType;

    private int orderNo;

}
