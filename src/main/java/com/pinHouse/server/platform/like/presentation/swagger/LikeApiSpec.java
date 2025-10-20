package com.pinHouse.server.platform.like.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.complex.application.dto.response.ComplexLikeResponse;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "좋아요 API", description = "좋아요 생성/조회/삭제 API 입니다.")
public interface LikeApiSpec {

    @Operation(
            summary = "좋아요 생성",
            description = "좋아요를 생성하는 API 입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "공고 좋아요 예시", value = NOTICE_EXAMPLE),
                                    @ExampleObject(name = "임대주택 좋아요 예시", value = COMPLEX_EXAMPLE),
                            }
                    )
            )
    )
    ApiResponse<Void> like(
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    @Operation(
            summary = "좋아요 한 공고 목록 조회",
            description = "좋아요 누른 공고를 조회하는 API 입니다."
    )
    ApiResponse<List<NoticeListResponse>> getLikeNotices(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "좋아요 한 임대주택 목록 조회",
            description = "좋아요 누른 임대주택을 조회하는 API 입니다."
    )
    ApiResponse<List<ComplexLikeResponse>> getLikeComplexes(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );

    @Operation(
            summary = "좋아요 취소",
            description = "좋아요를 취소하는 API 입니다."
    )
    ApiResponse<Void> disLike(@PathVariable Long id,
                              @AuthenticationPrincipal PrincipalDetails principalDetails);



    /// 공고 좋아요 예시
    String NOTICE_EXAMPLE = """
            {
                "targetId": "18442",
                "type": "NOTICE"
              }
            
            """;

    /// 방 좋아요 예시
    String COMPLEX_EXAMPLE = """
            {
              "targetId": "18442#221A",
              "type": "ROOM"
            }
            """;
}
