package com.pinHouse.server.platform.search.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.search.application.dto.FastSearchRequest;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "빠른 검색 API", description = "빠른 검색을 지원하는 API 입니다.")
public interface FastSearchApiSpec {

    @Operation(
            summary = "빠른 검색 API",
            description = "빠른 검색 API 입니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = {
                                    @ExampleObject(name = "빠른 검색 예시", value = SUCCESS_PAYLOAD),
                            }
                    )
            )
    )
    ApiResponse<List<UnitType>> search(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestBody FastSearchRequest request);




    /**
     * SUCCESS_PAYLOAD 응답
     */
    String SUCCESS_PAYLOAD = """
            {
                "count": 1,
                "minSize": 30.1,
                "maxSize": 84.2,
                "minPrice": 300000,
                "maxPrice": 5000000,
                "pinPointId": 1,
                "transitTime": 120,
                "facilities": [
                  "도서관"
                ],
                "supplyTypes": [
                  "청년"
                ],
                "rentalTypes": [
                  "공공임대"
                ]
              }
            """;

}
