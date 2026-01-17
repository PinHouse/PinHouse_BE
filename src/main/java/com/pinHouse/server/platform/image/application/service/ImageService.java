package com.pinHouse.server.platform.image.application.service;

import com.pinHouse.server.core.exception.code.ImageErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlRequest;
import com.pinHouse.server.platform.image.application.dto.PresignedUrlResponse;
import com.pinHouse.server.platform.image.application.usecase.ImageUseCase;
import com.pinHouse.server.platform.image.domain.entity.ImageType;
import com.pinHouse.server.platform.image.external.PresignedUrlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 이미지 업로드 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService implements ImageUseCase {

    private final PresignedUrlGenerator presignedUrlGenerator;

    @Value("${aws.s3.presigned-url.max-file-size-mb:5}")
    private int maxFileSizeMb;

    @Value("${aws.s3.presigned-url.expiration-minutes:10}")
    private int expirationMinutes;

    /**
     * 프로필 이미지 업로드를 위한 Presigned URL 생성
     */
    @Override
    public PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request, UUID userId) {

        // 1. 파일 크기 검증
        validateFileSize(request.fileSize());

        // 2. Content-Type 검증
        validateContentType(request.contentType());

        // 3. 파일 확장자 추출 및 검증
        String extension = extractExtension(request.fileName());
        validateExtension(extension);

        // 4. Object Key 생성: profile/{userId}/{uuid}.{ext}
        String objectKey = generateObjectKey(ImageType.PROFILE, userId, extension);

        // 5. Presigned URL 생성
        String presignedUrl = presignedUrlGenerator.generatePutPresignedUrl(objectKey, request.contentType());

        // 6. Public URL 생성
        String publicUrl = presignedUrlGenerator.getPublicUrl(objectKey);

        // 7. Response 반환
        int expiresInSeconds = expirationMinutes * 60;

        log.info("Presigned URL 생성 완료: userId={}, fileName={}, objectKey={}", userId, request.fileName(), objectKey);

        return PresignedUrlResponse.of(presignedUrl, publicUrl, expiresInSeconds);
    }

    // =================
    //  내부 로직
    // =================

    /**
     * 파일 크기 검증
     */
    private void validateFileSize(long fileSize) {
        long maxSizeBytes = maxFileSizeMb * 1024L * 1024L;
        if (fileSize > maxSizeBytes) {
            log.warn("파일 크기 초과: fileSize={} bytes, maxSize={} bytes", fileSize, maxSizeBytes);
            throw new CustomException(ImageErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    /**
     * Content-Type 검증
     */
    private void validateContentType(String contentType) {
        List<String> allowedContentTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/gif");

        if (!allowedContentTypes.contains(contentType.toLowerCase())) {
            log.warn("지원하지 않는 Content-Type: {}", contentType);
            throw new CustomException(ImageErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * 파일 확장자 추출
     */
    private String extractExtension(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new CustomException(ImageErrorCode.INVALID_FILE_NAME);
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex <= 0 || lastDotIndex == fileName.length() - 1) {
            log.warn("파일 확장자 추출 실패: fileName={}", fileName);
            throw new CustomException(ImageErrorCode.INVALID_FILE_EXTENSION);
        }

        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 파일 확장자 검증
     */
    private void validateExtension(String extension) {
        List<String> allowedExtensions = List.of("jpg", "jpeg", "png", "gif");

        if (!allowedExtensions.contains(extension)) {
            log.warn("지원하지 않는 확장자: {}", extension);
            throw new CustomException(ImageErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /**
     * Object Key 생성: {imageType}/{userId}/{uuid}.{extension}
     */
    private String generateObjectKey(ImageType imageType, UUID userId, String extension) {
        String uniqueId = UUID.randomUUID().toString();
        return String.format("%s/%s/%s.%s",
                imageType.getPath(),
                userId.toString(),
                uniqueId,
                extension);
    }
}
