package com.pinHouse.server.platform.like.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
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

@Tag(name = "좋아요 API", description = "좋아요 생성/조회/삭제 API 입니다.")
public interface LikeApiSpec {

    /// 좋아요 생성
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


    /// 좋아요 취소
    @Operation(
            summary = "좋아요 취소",
            description = "좋아요를 취소하는 API 입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "공고 좋아요 취소 예시", value = NOTICE_EXAMPLE),
                                    @ExampleObject(name = "임대주택 좋아요 취소 예시", value = COMPLEX_EXAMPLE),
                            }
                    )
            )
    )
    ApiResponse<Void> disLike(
            @RequestBody @Valid LikeRequest request,
            @AuthenticationPrincipal PrincipalDetails principalDetails);

    /// 공고 좋아요 예시
    String NOTICE_EXAMPLE = """
            {
                "targetId": "19417",
                "type": "NOTICE"
              }
            
            """;

    /// 방 좋아요 예시
    String COMPLEX_EXAMPLE = """
            {
              "targetId": "4b30ca7d718d4ea9a9f6966f",
              "type": "ROOM"
            }
            """;
}
