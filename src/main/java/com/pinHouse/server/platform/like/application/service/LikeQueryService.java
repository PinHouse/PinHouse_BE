package com.pinHouse.server.platform.like.application.service;

import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.like.domain.Like;
import com.pinHouse.server.platform.like.domain.LikeJpaRepository;
import com.pinHouse.server.platform.like.domain.LikeType;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeQueryService implements LikeQueryUseCase {

    private final LikeJpaRepository repository;
    private final UserUseCase userService;

    /// 나의 좋아요 공고 목록 Id 조회
    @Override
    @Transactional
    public List<String> getLikeNoticeIds(UUID userId) {

        /// 유저 조회
        User user = userService.loadUser(userId);

        /// 목록 조회 하기
        List<Like> likes = repository.findByUser_IdAndType(user.getId(), LikeType.NOTICE);

        /// 목록 조회한 아이디 한번에 조회하기
        return likes.stream()
                .map(Like::getTargetId)
                .toList();
    }


    /// 나의 좋아요 방 목록 Id 조회
    @Override
    public List<String> getLikeUnitTypeIds(UUID userId) {

        /// 유저 조회
        User user = userService.loadUser(userId);

        /// 방 목록 조회
        List<Like> likes = repository.findByUser_IdAndType(user.getId(), LikeType.ROOM);

        /// 목록 조회한 아이디 한번에 조회하기
        return likes.stream()
                .map(Like::getTargetId)
                .toList();
    }
}
