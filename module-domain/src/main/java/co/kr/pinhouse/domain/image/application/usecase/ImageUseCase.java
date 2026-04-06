package co.kr.pinhouse.domain.image.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.domain.image.application.dto.PresignedUrlRequest;
import co.kr.pinhouse.domain.image.application.dto.PresignedUrlResponse;

/**
 * 이미지 업로드 UseCase 인터페이스
 */
public interface ImageUseCase {

	/**
	 * 프로필 이미지 업로드를 위한 Presigned URL 생성
	 *
	 * @param request 파일 메타데이터 (fileName, contentType)
	 * @param userId  인증된 사용자 ID
	 * @return Presigned URL 정보 (업로드 URL, 최종 이미지 URL, 만료시간)
	 */
	PresignedUrlResponse generatePresignedUrl(PresignedUrlRequest request, UUID userId);
}
