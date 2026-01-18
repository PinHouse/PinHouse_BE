package com.pinHouse.server.platform.image.external;

import com.pinHouse.server.core.exception.code.ImageErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

/**
 * AWS S3 Presigned URL 생성 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3PresignedUrlGenerator implements PresignedUrlGenerator {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${aws.s3.presigned-url.expiration-minutes:10}")
    private int expirationMinutes;

    /**
     * PUT 요청을 위한 Presigned URL 생성
     */
    @Override
    public String generatePutPresignedUrl(String objectKey, String contentType) {
        try (S3Presigner presigner = S3Presigner.builder()
                .region(s3Client.serviceClientConfiguration().region())
                .build()) {

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .putObjectRequest(putObjectRequest)
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            String presignedUrl = presignedRequest.url().toString();

            log.info("Presigned URL 생성 성공: objectKey={}, expiresIn={}분", objectKey, expirationMinutes);

            return presignedUrl;

        } catch (S3Exception e) {
            log.error("S3 Presigned URL 생성 실패: objectKey={}, error={}", objectKey, e.getMessage());
            throw new CustomException(ImageErrorCode.S3_PRESIGNED_URL_GENERATION_FAILED);
        } catch (Exception e) {
            log.error("S3 Presigned URL 생성 중 예외 발생: objectKey={}, error={}", objectKey, e.getMessage());
            throw new CustomException(ImageErrorCode.S3_CLIENT_ERROR);
        }
    }

    /**
     * Public URL 생성
     */
    @Override
    public String getPublicUrl(String objectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                objectKey);
    }
}
