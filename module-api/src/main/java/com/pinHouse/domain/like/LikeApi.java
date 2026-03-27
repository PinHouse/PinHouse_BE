package com.pinHouse.domain.like;

import java.util.UUID;

import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.like.application.dto.LikeRequest;
import com.pinHouse.domain.like.application.usecase.LikeCommandUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
            @CurrentUserId(required = true) UUID userId) {

        /// 서비스 호출
        service.saveLike(userId, request);

        /// 리턴
        return ApiResponse.created();

    }

    /// 좋아요 취소
    @DeleteMapping
    public ApiResponse<Void> disLike(
            @RequestBody @Valid LikeRequest request,
            @CurrentUserId(required = true) UUID userId) {

        /// 서비스 호출
        service.deleteLike(userId, request);

        /// 리턴
        return ApiResponse.deleted();
    }

}
