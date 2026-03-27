package com.pinHouse.domain.pinPoint.presentation.swagger;

import java.util.UUID;

import com.pinHouse.common.auth.CurrentUserId;
import com.pinHouse.common.response.ApiResponse;
import com.pinHouse.domain.pinPoint.application.dto.PinPointListResponse;
import com.pinHouse.domain.pinPoint.application.dto.PinPointRequest;
import com.pinHouse.domain.pinPoint.application.dto.UpdatePinPointRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
            @CurrentUserId(required = true) UUID userId,
            @RequestBody @Valid PinPointRequest request);

    /// 목록 조회
    @Operation(
            summary = "핀포인트 목록조회 API",
            description = "나의 핀포인트 목록 들을 조회하는 API 입니다."
    )
    ApiResponse<PinPointListResponse> getPinPoints(
            @CurrentUserId(required = true) UUID userId
    );


    /// 수정
    @Operation(
            summary = "핀포인트 수정 API",
            description = "나의 핀포인트를 수정하는 API 입니다."
    )
    ApiResponse<Void> updatePinPoint(
            @PathVariable String id,
            @CurrentUserId(required = true) UUID userId,
            @RequestBody @Valid UpdatePinPointRequest request);


    /// 삭제
    @Operation(
            summary = "핀포인트 삭제 API",
            description = "나의 핀포인트를 삭제하는 API 입니다."
    )
    ApiResponse<Void> deletePinPoint(
            @RequestParam String id,
            @CurrentUserId(required = true) UUID userId);


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
