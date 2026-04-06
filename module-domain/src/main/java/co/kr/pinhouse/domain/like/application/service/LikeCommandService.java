package co.kr.pinhouse.domain.like.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.kr.pinhouse.common.exception.code.LikeErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.housing.complex.application.usecase.ComplexUseCase;
import co.kr.pinhouse.domain.housing.notice.application.usecase.NoticeUseCase;
import co.kr.pinhouse.domain.like.application.dto.LikeRequest;
import co.kr.pinhouse.domain.like.application.usecase.LikeCommandUseCase;
import co.kr.pinhouse.domain.like.domain.Like;
import co.kr.pinhouse.domain.like.domain.LikeJpaRepository;
import co.kr.pinhouse.domain.user.application.usecase.UserUseCase;
import co.kr.pinhouse.domain.user.domain.entity.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeCommandService implements LikeCommandUseCase {

	/// 레포지토리
	private final LikeJpaRepository repository;

	/// 의존성
	private final UserUseCase userService;
	private final ComplexUseCase complexService;
	private final NoticeUseCase noticeService;

	// =================
	//  퍼블릭 로직
	// =================

	/// 좋아요 저장
	@Override
	@Transactional
	public void saveLike(UUID userId, LikeRequest request) {

		/// 유저 검증
		User user = userService.loadUser(userId);

		/// 대상 존재 검증
		switch (request.type()) {
			case NOTICE -> noticeService.loadNotice(request.targetId());
			case ROOM -> complexService.loadComplexByUnitTypeId(request.targetId());
			default -> throw new CustomException(LikeErrorCode.BAD_REQUEST_LIKE);

		}

		/// 중복 조회
		if (repository.existsByUserIdAndTargetIdAndType(userId, request.targetId(), request.type())) {
			throw new CustomException(LikeErrorCode.DUPLICATE_LIKE);
		}

		/// 엔티티 생성 및 저장
		Like like = Like.of(user, request.targetId(), request.type());
		repository.save(like);
	}



	/// 좋아요 취소
	@Override
	@Transactional
	public void deleteLike(UUID userId, LikeRequest request) {

		/// 유저 검증
		userService.loadUser(userId);

		/// 존재 여부 체크 (영속성 컨테이너)
		Like like = repository.findByUser_IdAndTargetIdAndType(userId, request.targetId(), request.type())
				.orElseThrow(() -> new CustomException(LikeErrorCode.NOT_FOUND_LIKE));

		/// DB에서 삭제
		repository.delete(like);
	}

	// =================
	//  내부 로직
	// =================

}
