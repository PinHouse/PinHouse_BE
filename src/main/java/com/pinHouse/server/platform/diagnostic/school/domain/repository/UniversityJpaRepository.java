package com.pinHouse.server.platform.diagnostic.school.domain.repository;

import com.pinHouse.server.platform.diagnostic.school.domain.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UniversityJpaRepository extends JpaRepository<University, String> {

    /**
     * 대학교 이름으로 부분 검색 (LIKE %keyword%)
     * @param keyword 검색 키워드
     * @return 검색된 대학교 목록
     */
    List<University> findBySchoolNameContaining(String keyword);

    boolean existsBySchoolName(String schoolName);

    Optional<University> findBySchoolName(String schoolName);

}
