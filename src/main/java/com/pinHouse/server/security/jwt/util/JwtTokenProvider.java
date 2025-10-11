package com.pinHouse.server.security.jwt.util;

import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

import static com.pinHouse.server.security.jwt.util.TokenNameUtil.ID_CLAIM;
import static com.pinHouse.server.security.jwt.util.TokenNameUtil.ROLE_CLAIM;

/**
 * 토큰 발급기
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecretKey secretKey;

    @Value("${auth.jwt.access.expiration}")
    private Long accessExpiration;

    @Value("${auth.jwt.refresh.expiration}")
    private Long refreshExpiration;


    // Access token 발급
    public String generateAccessToken(Authentication authentication) {
        return generateToken(authentication, accessExpiration);
    }

    // Refresh token 발급
    public String generateRefreshToken(Authentication authentication) {
        return generateToken(authentication, refreshExpiration);
    }

    /// 토큰 생성 함수
    public String generateToken(Authentication authentication, long expireTime) {

        /// 시간 설정
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + expireTime);

        /// 인증에서 객체 가져오기
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        /// 권한 리스트 추출
        Collection<? extends GrantedAuthority> collection = principalDetails.getAuthorities();

        // String 형태로 변환
        List<String> authorities = collection.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        /// JWT 내용 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put(ID_CLAIM, principalDetails.getId());
        claims.put(ROLE_CLAIM, authorities);

        // 토큰 반환
        return Jwts.builder()
                .setSubject(String.valueOf(principalDetails.getId())) // 사용자 Id
                .setClaims(claims)
                .setIssuedAt(now)                                // 발급 시간
                .setExpiration(expiredDate)                      // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS512)   // 서명
                .compact();
    }


}
