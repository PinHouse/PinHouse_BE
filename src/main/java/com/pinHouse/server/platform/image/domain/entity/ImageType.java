package com.pinHouse.server.platform.image.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 이미지 타입 열거형
 */
@Getter
@RequiredArgsConstructor
public enum ImageType {

    /**
     * 프로필 이미지
     */
    PROFILE("profile");

    /**
     * S3 Object Key의 prefix로 사용되는 경로
     */
    private final String path;
}
