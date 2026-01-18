package com.pinHouse.server.platform.image.external;

/**
 * Presigned URL 생성 인터페이스
 * 테스트 가능성을 위한 의존성 역전
 */
public interface PresignedUrlGenerator {

    /**
     * PUT 요청을 위한 Presigned URL 생성
     *
     * @param objectKey S3 Object Key (예: profile/{userId}/{uuid}.jpg)
     * @param contentType Content-Type (예: image/jpeg)
     * @return Presigned URL (클라이언트가 PUT 요청할 URL)
     */
    String generatePutPresignedUrl(String objectKey, String contentType);

    /**
     * Public URL 생성
     *
     * @param objectKey S3 Object Key
     * @return Public URL (User.profileImage에 저장할 최종 URL)
     */
    String getPublicUrl(String objectKey);
}
