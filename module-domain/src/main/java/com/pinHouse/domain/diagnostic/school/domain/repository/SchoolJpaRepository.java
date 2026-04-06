package com.pinHouse.domain.diagnostic.school.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pinHouse.domain.diagnostic.school.domain.entity.School;

public interface SchoolJpaRepository extends JpaRepository<School, String> {

	/**
	 * 학교 이름으로 부분 검색 (LIKE %keyword%)
	 * 키워드로 시작하는 학교를 우선 정렬
	 * @param keyword 검색 키워드
	 * @return 검색된 학교 목록 (시작하는 것 우선)
	 */
	@Query("SELECT s FROM School s WHERE s.schoolName LIKE %:keyword% " +
		"ORDER BY CASE WHEN s.schoolName LIKE :keyword% THEN 0 ELSE 1 END, s.schoolName")
	List<School> findBySchoolNameContaining(@Param("keyword") String keyword);

	boolean existsBySchoolName(String schoolName);
}
