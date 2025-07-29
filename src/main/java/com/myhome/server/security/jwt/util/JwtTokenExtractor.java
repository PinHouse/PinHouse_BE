package com.myhome.server.security.jwt.util;

import com.myhome.server.core.response.response.ErrorCode;
import com.myhome.server.platform.application.out.user.UserPort;
import com.myhome.server.platform.domain.user.User;
import com.myhome.server.security.jwt.exception.JwtAuthenticationException;
import com.myhome.server.security.oauth2.domain.PrincipalDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

import static com.myhome.server.security.jwt.util.TokenNameUtil.*;
import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenExtractor {

    @Value("${kikihi.jwt.key}")
    private String key;

    private SecretKey secretKey;

    /// 의존성
    private final UserPort userPort;
    private final CookieUtil cookieUtil;


    /// JWT 의존성은 SecretKey 필요
    @PostConstruct
    private void setSecretKey() {
        secretKey = Keys.hmacShaKeyFor(key.getBytes());
    }

    /// 토큰 추출하기
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return cookieUtil.getAccessTokenFromCookie(request);
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return cookieUtil.getRefreshTokenFromCookie(request);
    }

    /// 내부 함수
    private Claims parseClaims(String token) {
        try {
            // JWT 파서를 빌드하고 서명된 토큰을 파싱
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)  // 서명 키를 설정
                    .build()
                    .parseClaimsJws(token)  // 서명된 JWT 토큰을 파싱
                    .getBody();  // Claims 객체 반환
        } catch (ExpiredJwtException e) {
            return e.getClaims();

        } catch (MalformedJwtException e) {
            throw new JwtAuthenticationException(INVALID_TOKEN);
        }
    }

    /// 검증 여부
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_NOT_FOUND.getMessage()); // 여기가 문제라면 메시지 바꿔도 좋음
        }
    }

    public Authentication getAuthentication(String token) {

        /// JWT 파싱
        Claims claims = parseClaims(token);

        /// 권한 정보 가져오기
        List<String> authoritiesList = (List<String>) claims.get(ROLE_CLAIM);

        // 권한을 SimpleGrantedAuthority로 변환
        Collection<? extends GrantedAuthority> authorities =
                authoritiesList.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        // userId를 String 변환
        String claimUserId = claims.get(ID_CLAIM, String.class);

        /// UUID 변환
        UUID userId = UUID.fromString(claimUserId);

        // 해당 userId로 Member를 조회
        User user = userPort.loadUserById(userId)
                .orElseThrow(() -> new JwtAuthenticationException(ErrorCode.USER_NOT_FOUND_IN_COOKIE.getMessage()));

        PrincipalDetails details = PrincipalDetails.of(user);

        /// SecurityContext에 저장하기 위한 UsernamePasswordAuthenticationToken 반환
        return new UsernamePasswordAuthenticationToken(details, token, authorities);
    }

    /// @Getter
    // 사용자 정보 추출
    public String getId(String token) {
        return getIdFromToken(token, ID_CLAIM);
    }

    public String getEmail(String token) {
        return getClaimFromToken(token, EMAIL_CLAIM);
    }

    public String getRole(String token) {
        return getClaimFromToken(token, ROLE_CLAIM);
    }

    public Boolean isExpired(String token) {
        Claims claims = parseClaims(token);
        return claims.getExpiration().before(new Date());
    }

    private String getClaimFromToken(String token, String claimName) {
        Claims claims = parseClaims(token);
        return claims.get(claimName, String.class);
    }

    private String getIdFromToken(String token, String claimName) {
        Claims claims = parseClaims(token);
        return claims.get(claimName, String.class);
    }
}
