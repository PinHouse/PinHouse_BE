package com.pinHouse.server.platform.diagnostic.school.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "school")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class School {

    @Id
    private String id;

    private String schoolName;

    private String schoolType;

    private String schoolCategory;

    private String sidoCode;

    private String sidoName;

    private String sigunguName;
}
