package com.pinHouse.common.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 회원가입을 위한 임시 사용자 정보
 * OAuth2 인증 후 회원가입 시 필요한 정보를 Redis에 임시 저장하기 위한 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempUserInfo implements Serializable {
    private String socialId;
    private String social;
    private String email;
    private String username;
    private String gender;
    private String birthyear;
    private String birthday;
    private String imageUrl;
}
