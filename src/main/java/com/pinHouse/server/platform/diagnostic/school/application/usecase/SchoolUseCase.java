package com.pinHouse.server.platform.diagnostic.school.application.usecase;

import com.pinHouse.server.platform.diagnostic.school.application.dto.SchoolResponse;

import java.util.List;

public interface SchoolUseCase {

    /// 고등학교 검색 로직
    List<SchoolResponse> searchSchool(String schoolName);

    /// 고등학교 가능한건지
    String existSchool(String schoolName);

    /// 대학교 검색 로직
    List<SchoolResponse> searchUniversity(String universityName);

    /// 대학교 가능한건지
    String existUniversity(String schoolName);

}
