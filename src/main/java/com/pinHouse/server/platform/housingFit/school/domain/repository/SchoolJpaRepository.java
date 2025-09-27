package com.pinHouse.server.platform.housingFit.school.domain.repository;

import com.pinHouse.server.platform.housingFit.school.domain.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SchoolJpaRepository extends JpaRepository<School, String> {

    @Query(
            value = """
        SELECT * FROM school 
        WHERE MATCH(school_name) 
        AGAINST(:keyword)
        """,
            nativeQuery = true
    )
    List<School> searchBySchoolName(@Param("keyword") String keyword);


    boolean existsBySchoolName(String schoolName);
}
