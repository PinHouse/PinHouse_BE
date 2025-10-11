package com.pinHouse.server.security.auth.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.security.auth.application.usecase.AuthUseCase;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenRequest;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenResponse;
import com.pinHouse.server.security.jwt.application.exception.JwtAuthenticationException;
import com.pinHouse.server.security.jwt.application.util.JwtProvider;
import com.pinHouse.server.security.jwt.application.util.JwtValidator;
import com.pinHouse.server.security.jwt.domain.entity.JwtToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    /// 유저 저장소
    private final UserJpaRepository repository;

    /// 토큰 의존성
    private final JwtValidator jwtValidator;
    private final JwtProvider jwtProvider;

    /// 로그아웃
    @Override
    @Transactional(readOnly = true)
    public void logout(UUID userId, Optional<String> refreshToken) {

        /// DB 검증
        User user = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 고유번호를 가진 유저가 없습니다"));

        /// 없다면 예외처리
        if (refreshToken.isEmpty()) {
            throw new JwtAuthenticationException(ErrorCode.REFRESH_INVALID_LOGIN);
        }

        /// 레디스에서 삭제하도록 로직 수행
        jwtValidator.removeRefreshToken(userId, refreshToken.get());
    }

    /// 토큰 재발급
    @Override
    @Transactional(readOnly = true)
    public JwtTokenResponse reissue(Optional<String> refreshToken) {

        /// 없다면 예외처리
        if (refreshToken.isEmpty()) {
            throw new JwtAuthenticationException(ErrorCode.REFRESH_INVALID_LOGIN);
        }

        /// 존재하는 리프레쉬 토큰 검증
        JwtToken token = jwtValidator.validateRefreshToken(refreshToken.get());

        /// 리프레쉬 토큰 바탕으로 조회
        User user = repository.findById(token.getUserId())
                .orElseThrow(() -> new NoSuchElementException("해당 아이디를 가진 유저가 없습니다"));

        /// 인증된 유저에게 JWT 발급하기
        var jwtRequest = JwtTokenRequest.from(user);

        String newAccessToken = jwtProvider.createAccessToken(jwtRequest);

        return JwtTokenResponse.of(newAccessToken, null);
    }

}
