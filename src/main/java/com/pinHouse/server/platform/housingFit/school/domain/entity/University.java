package com.pinHouse.server.platform.housingFit.school.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "university")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class University {

    @Id
    private String id;

    private String schoolName;

    private String campusType;

    private String collegeType;

    private Integer sidoCode;

    private String sidoName;

    private String sigunguName;

}
