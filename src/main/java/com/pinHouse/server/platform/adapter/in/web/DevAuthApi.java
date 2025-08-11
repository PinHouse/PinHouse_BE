package com.pinHouse.server.platform.adapter.in.web;

import com.pinHouse.server.core.response.response.ApiResponse;
import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.adapter.in.web.swagger.DevAuthApiSpec;
import com.pinHouse.server.platform.application.out.user.UserPort;
import com.pinHouse.server.platform.domain.user.Provider;
import com.pinHouse.server.platform.domain.user.Role;
import com.pinHouse.server.platform.domain.user.User;
import com.pinHouse.server.security.jwt.service.JwtTokenUseCase;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;
import java.util.UUID;

@Profile("!prod")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class DevAuthApi implements DevAuthApiSpec {

    private final UserPort userPort;
    private final JwtTokenUseCase tokenService;

    // 테스트용으로 만든 UUID
    private final UUID id = UUID.fromString("12345678-aaaa-bbbb-cccc-123456789abc");



    @PostMapping("/dev-login")
    public ApiResponse<Void> devLogin(HttpServletResponse httpServletResponse) {

        /// 테스트용 유저 정보 수정
        User user;

        if (userPort.checkExistingById(id)) {
            user = userPort.loadUserById(id)
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));
        } else {
            User dev = createDev();

            user = userPort.saveUser(dev);
        }

        /// PrincipalDetails 생성 (시스템에 따라 다름)
        PrincipalDetails principalDetails = PrincipalDetails.of(user);

        /// Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principalDetails, null, principalDetails.getAuthorities()
        );

        tokenService.createAccessToken(httpServletResponse, authentication);
        tokenService.createRefreshToken(httpServletResponse, authentication);

        return ApiResponse.created();
    }

    /// 임시 유저 생성
    private User createDev() {
        return User.builder()
                .id(id)
                .socialId("dev-naver-id")
                .email("pinhouse_naver@example.com")
                .profileImage("http://image-url")
                .phoneNumber("010-1111-1111")
                .name("naver개발자")
                .provider(Provider.NAVER) // 테스트용 값 (Enum)
                .role(Role.ADMIN) // 관리자 권한 부여
                .build();
    }

}

