package com.pinHouse.server.platform.diagnostic.school.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.diagnostic.school.application.dto.SchoolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "학교 API", description = "학교를 조회하는 API 입니다.")
public interface SchoolApiSpec {

    @Operation(
            summary = "고등학교 검색 API",
            description = "키워드를 바탕으로 고등학교를 조회하는 API 입니다."
    )
    ApiResponse<List<SchoolResponse>> getSchools(
            @Parameter(example = "태원고등학교")
            @RequestParam String keyword);

    @Operation(
            summary = "고등학교 가능여부 API",
            description = "키워드를 바탕으로 해당 고등학교가 가능한지 조회하는 API 입니다."
    )
    ApiResponse<String> checkShcool(
            @Parameter(example = "태원고등학교")
            @RequestParam String keyword);

    @Operation(
            summary = "대학교 검색 API",
            description = "키워드를 바탕으로 대학교를 조회하는 API 입니다."
    )
    ApiResponse<List<SchoolResponse>> getUnivs(
            @Parameter(example = "가천대학교")
            @RequestParam String keyword);


    @Operation(
            summary = "대학교 가능여부 API",
            description = "키워드를 바탕으로 해당 대학교가 가능한지 조회하는 API 입니다."
    )
    ApiResponse<String> checkUniv(
            @Parameter(example = "가천대학교")
            @RequestParam String keyword);

}
