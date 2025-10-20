package com.pinHouse.server.platform.like.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;
import com.pinHouse.server.platform.like.application.usecase.LikeCommandUseCase;
import com.pinHouse.server.platform.like.domain.Like;
import com.pinHouse.server.platform.like.domain.LikeRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeCommandService implements LikeCommandUseCase {

    /// 레포지토리
    private final LikeRepository repository;

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
            case ROOM -> complexService.loadComplex(request.targetId());
            default -> throw new IllegalStateException(ErrorCode.BAD_REQUEST.getMessage());
        }

        /// 중복 조회
        if (repository.existsByUserIdAndTargetIdAndType(userId, request.targetId(), request.type())) {
            throw new IllegalStateException(ErrorCode.DUPLICATE_LIKE.getMessage());
        }

        /// 엔티티 생성 및 저장
        Like like = Like.of(user, request.targetId(), request.type());
        repository.save(like);
    }



    /// 좋아요 취소
    @Override
    @Transactional
    public void deleteLike(Long id, UUID userId) {

        /// 유저 예외처리

        /// 존재 여부 체크 (영속성 컨테이너)
        Like like = repository.findByIdAndUser_Id(id, userId)
                .orElseThrow();

        /// DB에서 삭제
        repository.delete(like);
    }

    // =================
    //  내부 로직
    // =================

}
