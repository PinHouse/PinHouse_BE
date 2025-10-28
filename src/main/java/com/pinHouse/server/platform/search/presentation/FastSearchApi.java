package com.pinHouse.server.platform.search.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.platform.search.application.usecase.FastSearchUseCase;
import com.pinHouse.server.platform.search.presentation.swagger.FastSearchApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/search/fast")
public class FastSearchApi implements FastSearchApiSpec {

    private final FastSearchUseCase service;

    /**
     * 빠른 검색 API
     *
     * @param request 요청 DTO
     */
    @PostMapping
    public ApiResponse<List<UnitType>> search(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid FastSearchRequest request) {

        /// 서비스
        List<UnitType> search = service.search(principalDetails.getId(), request);

        /// 응답
        return ApiResponse.ok(search);
    }


}
