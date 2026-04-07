package co.kr.pinhouse.domain.like.application.usecase;

import java.util.UUID;

import co.kr.pinhouse.domain.like.application.dto.request.LikeRequest;

public interface LikeCommandUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 좋아요 저장
	void saveLike(UUID userId, LikeRequest request);

	/// 좋아요 취소
	void deleteLike(UUID userId, LikeRequest request);

	// =================
	//  외부 로직
	// =================

}
