package com.pinHouse.server.security.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Configuration
public class SecretKeyConfig {

    @Value("${auth.jwt.secret}")
    private String base64Secret;

    /**
     * Bean을 통해 비밀키 초기화
     */
    @Bean
    public SecretKey jwtSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
