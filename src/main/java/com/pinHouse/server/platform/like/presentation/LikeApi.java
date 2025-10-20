package com.pinHouse.server.platform.like.presentation;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.like.application.dto.ComplexLikeResponse;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;
import com.pinHouse.server.platform.like.application.usecase.LikeUseCase;
import com.pinHouse.server.platform.like.presentation.swagger.LikeApiSpec;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/v1/likes")
@RequiredArgsConstructor
public class LikeApi implements LikeApiSpec {

    private final LikeUseCase service;

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

    /// 나의 좋아요 공고 목록 조회
    @GetMapping("/notice")
    public ApiResponse<List<NoticeListResponse>> getLikeNotices(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스 호출
        var response = service.getNoticeLikes(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(response);
    }

    /// 나의 좋아요 주택 목록 조회
    @GetMapping("/complex")
    public ApiResponse<List<ComplexLikeResponse>> getLikeComplexes(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {

        /// 서비스 호출
        var response = service.getComplexesLikes(principalDetails.getId());

        /// 리턴
        return ApiResponse.ok(response);
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
