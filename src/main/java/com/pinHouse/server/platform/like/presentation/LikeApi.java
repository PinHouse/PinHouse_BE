package com.pinHouse.server.platform.like.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;
import com.pinHouse.server.platform.like.application.usecase.LikeCommandUseCase;
import com.pinHouse.server.platform.like.presentation.swagger.LikeApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeApi implements LikeApiSpec {

    private final LikeCommandUseCase service;

    /// 좋아요 생성
    @PostMapping
    public ApiResponse<Void> like(
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스 호출
        service.saveLike(principalDetails.getId(), request);

        /// 리턴
        return ApiResponse.created();

    }

    /// 좋아요 취소
    @DeleteMapping("/{id}")
    public ApiResponse<Void> disLike(@PathVariable Long id,
                                     @AuthenticationPrincipal PrincipalDetails principalDetails) {

        /// 서비스 호출
        service.deleteLike(id, principalDetails.getId());

        /// 리턴
        return ApiResponse.deleted();
    }

}
