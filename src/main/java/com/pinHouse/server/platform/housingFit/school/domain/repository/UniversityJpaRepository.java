package com.pinHouse.server.platform.housingFit.school.domain.repository;

import com.pinHouse.server.platform.housingFit.school.domain.entity.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UniversityJpaRepository extends JpaRepository<University, String> {

    @Query(
            value = """
        SELECT * FROM university 
        WHERE MATCH(school_name) 
        AGAINST(:keyword)
        """,
            nativeQuery = true
    )
    List<University> searchBySchoolName(@Param("keyword") String keyword);

    boolean existsBySchoolName(String schoolName);

    Optional<University> findBySchoolName(String schoolName);

}
