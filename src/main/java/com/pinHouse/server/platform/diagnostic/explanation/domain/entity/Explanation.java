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
public class Explanation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "question_id")
    private DiagnosisQuestion question;

    private String explanation;

    private String imageUrl;

    /// 정적 팩토리 메서드
    public static Explanation of(DiagnosisQuestion question, String explanation, String imageUrl) {
        return Explanation.builder()
                .explanation(explanation)
                .imageUrl(imageUrl)
                .question(question)
                .build();

    }

}
