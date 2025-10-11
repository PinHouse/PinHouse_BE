package com.pinHouse.server.platform.pinPoint.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.server.platform.pinPoint.application.dto.PinPointResponse;
import com.pinHouse.server.platform.pinPoint.application.dto.UpdatePinPointRequest;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "핀포인트 API", description = "핀포인트 생성/조회/삭제 API 입니다.")
public interface PinPointApiSpec {

    /// 저장
    @Operation(
            summary = "핀포인트 설정 API",
            description = "핀포인트 설정 API 입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "핀포인트 예시", value = SUCCESS_PAYLOAD),
                            }
                    )
            )
    )
    ApiResponse<Void> addPinPoint(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid PinPointRequest request);

    /// 목록 조회
    @Operation(
            summary = "핀포인트들 목록조회 API",
            description = "나의 핀포인트 목록 들을 조회하는 API 입니다."
    )
    ApiResponse<List<PinPointResponse>> getPinPoints(
            @AuthenticationPrincipal PrincipalDetails principalDetails
    );


    /// 수정
    @Operation(
            summary = "핀포인트들 수정 API",
            description = "나의 핀포인트를 수정하는 API 입니다."
    )
    ApiResponse<Void> updatePinPoint(
            @PathVariable Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody @Valid UpdatePinPointRequest request);


    /// 삭제
    @Operation(
            summary = "핀포인트들 삭제 API",
            description = "나의 핀포인트를 삭제하는 API 입니다."
    )
    ApiResponse<Void> deletePinPoint(
            @RequestParam Long id,
            @AuthenticationPrincipal PrincipalDetails principalDetails);


    /**
     * SUCCESS_PAYLOAD 응답
     */
    String SUCCESS_PAYLOAD = """
            {
               "address": "서울 중구 세종대로 110 서울특별시청",
               "name": "나의 시청",
               "first": true
             }
            """;
}
