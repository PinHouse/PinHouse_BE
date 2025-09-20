package com.pinHouse.server.platform.housingFit.school.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Table(name = "university")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class University {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schoolName;

    private String campusType;

    private String collegeType;

    private Integer sidoCode;

    private String sidoName;

    private String sigunguName;

}
