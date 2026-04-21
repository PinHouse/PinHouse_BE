package co.kr.pinhouse.infrastructure.image.external;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageException;

import co.kr.pinhouse.common.exception.code.ImageErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.image.external.PresignedUrlGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * GCS Signed URL 생성 구현체
 *
 * <p>Google Cloud Storage의 Signed URL을 생성하여 클라이언트가 직접 GCS에 이미지를 업로드할 수 있도록 합니다.
 * AWS S3 Presigned URL과 동일한 역할을 수행하며, PresignedUrlGenerator 인터페이스를 구현합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>PUT 요청용 임시 Signed URL 생성 (기본 10분 유효)</li>
 *   <li>영구적인 Public URL 생성 (DB 저장용)</li>
 *   <li>V4 서명 알고리즘 사용으로 보안 강화</li>
 * </ul>
 *
 * @see PresignedUrlGenerator
 * @see co.kr.pinhouse.domain.image.application.service.ImageService
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GcsSignedUrlGenerator implements PresignedUrlGenerator {

	/**
	 * GCS Storage 클라이언트
	 * GcsConfig 또는 GcsEmulatorConfig에서 주입됨
	 */
	private final Storage storage;

	/**
	 * GCS 버킷 이름
	 * 프로덕션: pinhouse-prod
	 * 로컬: pinhouse-be-local
	 */
	@Value("${gcs.bucket}")
	private String bucketName;

	/**
	 * Signed URL 만료 시간 (분 단위)
	 * 기본값: 10분
	 */
	@Value("${gcs.presigned-url.expiration-minutes:10}")
	private int expirationMinutes;

	/**
	 * PUT 요청을 위한 Signed URL 생성
	 *
	 * <p>클라이언트가 이 URL을 사용하여 직접 GCS에 이미지를 업로드할 수 있습니다.
	 * URL은 설정된 시간(기본 10분) 동안만 유효하며, PUT 메서드로만 사용 가능합니다.</p>
	 *
	 * @param objectKey GCS Object Key (예: profile/{userId}/{uuid}.jpg)
	 * @param contentType 이미지 Content-Type (예: image/jpeg)
	 * @return Signed URL 문자열
	 * @throws CustomException GCS_PRESIGNED_URL_GENERATION_FAILED - URL 생성 실패 시
	 * @throws CustomException GCS_CLIENT_ERROR - GCS 통신 오류 발생 시
	 */
	@Override
	public String generatePutPresignedUrl(String objectKey, String contentType) {
		try {
			// 1. BlobId 생성: 버킷명 + Object Key 조합
			BlobId blobId = BlobId.of(bucketName, objectKey);

			// 2. BlobInfo 생성: Content-Type 메타데이터 설정
			BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
				.setContentType(contentType)
				.build();

			// 3. Extension Headers 설정: PUT 요청 시 Content-Type 검증용
			Map<String, String> extensionHeaders = new HashMap<>();
			extensionHeaders.put("Content-Type", contentType);

			// 4. Signed URL 생성
			// - V4 서명 알고리즘 사용 (최신 버전)
			// - PUT 메서드만 허용
			// - 설정된 시간 동안만 유효
			URL signedUrl = storage.signUrl(
				blobInfo,
				expirationMinutes,
				TimeUnit.MINUTES,
				Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
				Storage.SignUrlOption.withExtHeaders(extensionHeaders),
				Storage.SignUrlOption.withV4Signature()
			);

			log.info("GCS Signed URL 생성 성공: objectKey={}, expiresIn={}분",
				sanitize(objectKey), sanitize(expirationMinutes));

			return signedUrl.toString();

		} catch (StorageException e) {
			// GCS API 호출 실패 (권한 문제, 네트워크 오류 등)
			log.error("GCS Signed URL 생성 실패: objectKey={}, error={}",
				sanitize(objectKey), sanitize(e.getMessage()));
			throw new CustomException(ImageErrorCode.GCS_PRESIGNED_URL_GENERATION_FAILED);
		} catch (Exception e) {
			// 기타 예외 (잘못된 파라미터, 내부 오류 등)
			log.error("GCS Signed URL 생성 중 예외 발생: objectKey={}, error={}",
				sanitize(objectKey), sanitize(e.getMessage()));
			throw new CustomException(ImageErrorCode.GCS_CLIENT_ERROR);
		}
	}

	/**
	 * Public URL 생성
	 *
	 * <p>GCS에 저장된 이미지의 영구적인 Public URL을 생성합니다.
	 * 이 URL은 User.profileImage 필드에 저장되어 API 응답에 사용됩니다.</p>
	 *
	 * <p>URL 형식: https://storage.googleapis.com/{bucket}/{objectKey}</p>
	 *
	 * @param objectKey GCS Object Key (예: profile/{userId}/{uuid}.jpg)
	 * @return Public URL 문자열
	 */
	@Override
	public String getPublicUrl(String objectKey) {
		// GCS의 표준 Public URL 형식
		// 버킷에 Public 읽기 권한이 설정되어 있어야 접근 가능
		return String.format("https://storage.googleapis.com/%s/%s", bucketName, objectKey);
	}
}
