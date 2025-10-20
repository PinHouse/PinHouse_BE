package com.pinHouse.server.platform.like.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.like.application.dto.ComplexLikeResponse;
import com.pinHouse.server.platform.like.application.dto.LikeRequest;
import com.pinHouse.server.platform.like.application.dto.NoticeLikeResponse;
import com.pinHouse.server.platform.like.application.usecase.LikeUseCase;
import com.pinHouse.server.platform.like.domain.Like;
import com.pinHouse.server.platform.like.domain.LikeRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService implements LikeUseCase {

    private final LikeRepository repository;
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
    public void deleteLike(Long id, UUID userId) {

    }

    /// 나의 좋아요 공고 목록 조회
    @Override
    public List<NoticeLikeResponse> getNoticeLikes(UUID userId) {

        return List.of();
    }

    /// 나의 좋아요 방 목록 조회
    @Override
    public List<ComplexLikeResponse> getComplexesLikes(UUID userId) {

        return List.of();
    }

    // =================
    //  내부 로직
    // =================

}
