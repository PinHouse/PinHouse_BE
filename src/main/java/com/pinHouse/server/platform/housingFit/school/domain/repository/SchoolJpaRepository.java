package com.pinHouse.server.platform.housingFit.school.domain.repository;

import com.pinHouse.server.platform.housingFit.school.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolJpaRepository extends JpaRepository<School, Long> {
}
