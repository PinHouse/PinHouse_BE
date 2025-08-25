package com.pinHouse.server.platform.adapter.out.jpa.school;

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
public class SchoolJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;






}
