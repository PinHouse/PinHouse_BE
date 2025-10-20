package com.pinHouse.server.security.jwt.application.util;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.security.jwt.domain.entity.JwtRefreshToken;
import com.pinHouse.server.security.jwt.domain.repository.JwtRefreshTokenRepository;
import com.pinHouse.server.security.jwt.application.exception.JwtAuthenticationException;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.NoSuchElementException;
import java.util.UUID;

import static com.pinHouse.server.core.util.RedisKeyUtil.ID_CLAIM;

@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final SecretKey secretKey;
    private final UserJpaRepository userRepository;
    /// 레디스 저장소
    private final JwtRefreshTokenRepository repository;


    // =================
    //  퍼블릭 로직
    // =================

    /// 액세스 토큰 검증
    public Authentication validateAccessToken(String accessToken) {

        try {
            /// 토큰 자체의 검증성 파악
            assertJwtValid(accessToken);

            /// 검증 완료되었다면 유저 정보 가져오기
            UUID userId = UUID.fromString(getUserIdFromToken(accessToken));

            /// 유저 예외처리
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_NOT_USER));


            /// 시큐리티에 넣을 인증 객체 생성
            PrincipalDetails principalDetails = PrincipalDetails.of(user);
            return new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());


        } catch (ExpiredJwtException e) {
            /// 만료된 토큰
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED);

        } catch (SignatureException e) {
            /// 잘못된 서명
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_SIGNATURE);

        } catch (MalformedJwtException e) {
            //// 구조가 깨진 토큰
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_MALFORMED);

        } catch (UnsupportedJwtException e) {
            /// 지원되지 않는 JWT 형식 (예: 압축/암호화된 JWT)
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_UNSUPPORTED);

        } catch (IllegalArgumentException e) {
            /// 토큰이 비어있거나 null
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_NOT_FOUND);

        } catch (JwtException e) {
            /// JWT 관련 기타 예외 (상위 클래스)
            throw new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_INVALID);

        } catch (JwtAuthenticationException e) {
            /// 커스텀 JWT 예외 (예: USER_NOT_FOUND 등)
            throw e;

        } catch (Exception e) {
            /// 예상치 못한 모든 예외
            throw e;
        }
    }

    /// 리프레쉬 토큰 검증
    public JwtRefreshToken validateRefreshToken(String refreshToken) {

        try {
            /// 토큰 자체의 유효성 검증 (서명, 만료일)
            assertJwtValid(refreshToken);

            /// 토큰에서 유저 ID 추출
            UUID userId = UUID.fromString(getUserIdFromToken(refreshToken));

            /// 유저 예외처리
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NoSuchElementException(ErrorCode.REFRESH_TOKEN_NOT_USER.getMessage()));

            /// 리턴
            return repository.findByUserIdAndRefreshToken(user.getId(), refreshToken)
                    .orElseThrow(() -> new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_NOT_FOUND));

        } catch (ExpiredJwtException e) {
            // 토큰이 '만료'된 경우의 처리
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            // 서명이 잘못된 경우의 처리
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            // 토큰 구조가 잘못된 경우의 처리
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_UNSUPPORTED);
        } catch (Exception e) {
            throw e;
        }
    }

    /// 로그아웃을 위해 사용하는 로직
    public void removeRefreshToken(UUID userId, String refreshToken) {

        /// 토큰 추출
        JwtRefreshToken token = validateRefreshToken(refreshToken);

        /// 토큰에서 유저 ID 추출
        UUID userIdFromAccessToken = token.getUserId();

        /// 유저가 다르다면, 예외 발생
        if (!userIdFromAccessToken.equals(userId)) {
            throw new JwtAuthenticationException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        /// 레디스에서 토큰 삭제
        repository.delete(token);

    }

    // =================
    //  내부 공통 로직
    // =================

    /// 비밀키로 해석 가능한지 검증
    private void assertJwtValid(String token) {
         Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
    }


    /// 토큰에서 유저ID 추출하기
    private String getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(ID_CLAIM, String.class);
    }

}
