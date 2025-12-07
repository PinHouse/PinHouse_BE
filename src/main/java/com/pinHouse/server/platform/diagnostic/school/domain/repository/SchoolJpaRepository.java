package com.pinHouse.server.platform.diagnostic.school.domain.repository;

import com.pinHouse.server.platform.diagnostic.school.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SchoolJpaRepository extends JpaRepository<School, String> {

    /**
     * 학교 이름으로 부분 검색 (LIKE %keyword%)
     * @param keyword 검색 키워드
     * @return 검색된 학교 목록
     */
    List<School> findBySchoolNameContaining(String keyword);

    boolean existsBySchoolName(String schoolName);
}
