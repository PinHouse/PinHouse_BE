package com.pinHouse.server.platform.diagnostic.school.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.school.application.dto.SchoolResponse;
import com.pinHouse.server.platform.diagnostic.school.application.usecase.SchoolUseCase;
import com.pinHouse.server.platform.diagnostic.school.presentation.swagger.SchoolApiSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class SchoolApi implements SchoolApiSpec {

    private final SchoolUseCase service;

    /**
     * 고등학교 조회
     * @param keyword   조회 단어
     */
    @GetMapping("/school/search")
    public ApiResponse<List<SchoolResponse>> getSchools(@RequestParam String keyword) {

        /// 서비스
        List<SchoolResponse> responses = service.searchSchool(keyword);

        /// 응답
        return ApiResponse.ok(responses);
    }

    /**
     * 대학교 가능한지 체크
     * @param keyword   조회 단어
     */
    @GetMapping("/school")
    public ApiResponse<String> checkShcool(@RequestParam String keyword) {

        /// 서비스
        String response = service.existSchool(keyword);

        /// 응답
        return ApiResponse.ok(response);
    }

    /**
     * 대학교 조회
     * @param keyword   조회 단어
     */
    @GetMapping("/univ/search")
    public ApiResponse<List<SchoolResponse>> getUnivs(@RequestParam String keyword) {

        /// 서비스
        List<SchoolResponse> responses = service.searchUniversity(keyword);

        /// 응답
        return ApiResponse.ok(responses);
    }

    /**
     * 대학교 가능한지 체크
     * @param keyword   조회 단어
     */
    @GetMapping("/univ")
    public ApiResponse<String> checkUniv(@RequestParam String keyword) {

        /// 서비스
        String response = service.existUniversity(keyword);

        /// 응답
        return ApiResponse.ok(response);
    }




}
