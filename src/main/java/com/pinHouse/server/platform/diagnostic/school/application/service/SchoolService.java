package com.pinHouse.server.platform.diagnostic.school.application.service;

import com.pinHouse.server.platform.diagnostic.school.application.dto.SchoolResponse;
import com.pinHouse.server.platform.diagnostic.school.application.usecase.SchoolUseCase;
import com.pinHouse.server.platform.diagnostic.school.domain.entity.School;
import com.pinHouse.server.platform.diagnostic.school.domain.entity.University;
import com.pinHouse.server.platform.diagnostic.school.domain.repository.SchoolJpaRepository;
import com.pinHouse.server.platform.diagnostic.school.domain.repository.UniversityJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SchoolService implements SchoolUseCase {

    private final SchoolJpaRepository schoolJpaRepository;
    private final UniversityJpaRepository universityRepository;


    /**
     * FulltextIndex 바탕으로 검색
     * @param schoolName    검색할 이름
     */
    @Override
    public List<SchoolResponse> searchSchool(String schoolName) {

        /// 목록
        List<School> responses = schoolJpaRepository.searchBySchoolName(schoolName);

        return SchoolResponse.from(responses);

    }

    /**
     * @param schoolName    고등학교 이름
     */
    @Override
    public String existSchool(String schoolName) {

        /// DB 조회
        boolean existed = schoolJpaRepository.existsBySchoolName(schoolName);

        /// 존재하는지 여부 체크
        if (!existed) {
            Optional<School> school = schoolJpaRepository.findById(schoolName);

            if(school.isEmpty()){
                return schoolName + "는 조회되지 않아요";
            }
        }

        /// 결과
        return existed ? schoolName + "는 공공임대주택에 지원 가능해요" : schoolName + "는 공공임대주택에 지원 불가해요";
    }

    /**
     * FulltextIndex 바탕으로 검색
     * @param universityName    검색할 이름
     */
    @Override
    public List<SchoolResponse> searchUniversity(String universityName) {

        /// 목록
        List<University> responses = universityRepository.searchBySchoolName(universityName);

        /// 응답
        return SchoolResponse.fromUniv(responses);

    }

    /**
     * 여부 체크
     * @param schoolName    대학교 이름
     */
    @Override
    public String existUniversity(String schoolName) {

        /// DB 조회
        boolean existed = universityRepository.existsBySchoolName(schoolName);

        /// 존재하는지 여부 체크
        if (!existed) {
            Optional<University> univ = universityRepository.findBySchoolName(schoolName);

            if(univ.isEmpty()){
                return schoolName + "는 조회되지 않아요";
            }
        }

        /// 결과
        return existed ? schoolName + "는 공공임대주택에 지원 가능해요" : schoolName + "는 공공임대주택에 지원 불가해요";
    }

}
