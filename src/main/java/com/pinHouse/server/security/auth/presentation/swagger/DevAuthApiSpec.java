package com.pinHouse.server.security.auth.presentation.swagger;

import com.pinHouse.server.core.response.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "개발용 JWT 발급 API", description = "개발용 JWT 발급 API입니다.")
public interface DevAuthApiSpec {

    /// 재발급
    @Operation(
            summary = "개발용 토큰 발급 API",
            description = "개발용 액세스 토큰(1시간) 및 리프레쉬 토큰을 즉시 발급 받을 수 있습니다."
    )
    ApiResponse<Void> devLogin(HttpServletResponse response);

}
