package com.pinHouse.server.platform.housingFit.school.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class School {

    private String id;

    private String schoolName;

    private String schoolType;

    private String schoolCategory;

    private String sidoCode;

    private String sidoName;

    private String sigunguName;
}
