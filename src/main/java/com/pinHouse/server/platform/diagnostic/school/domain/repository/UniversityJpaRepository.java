package com.pinHouse.server.platform.diagnostic.school.domain.repository;

import com.pinHouse.server.platform.diagnostic.school.domain.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UniversityJpaRepository extends JpaRepository<University, String> {

    /**
     * 대학교 이름으로 부분 검색 (LIKE %keyword%)
     * 키워드로 시작하는 대학교를 우선 정렬
     * @param keyword 검색 키워드
     * @return 검색된 대학교 목록 (시작하는 것 우선)
     */
    @Query("SELECT u FROM University u WHERE u.schoolName LIKE %:keyword% " +
           "ORDER BY CASE WHEN u.schoolName LIKE :keyword% THEN 0 ELSE 1 END, u.schoolName")
    List<University> findBySchoolNameContaining(@Param("keyword") String keyword);

    boolean existsBySchoolName(String schoolName);

    Optional<University> findBySchoolName(String schoolName);

}
