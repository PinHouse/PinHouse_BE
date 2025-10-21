package com.pinHouse.server.platform.like.application.service;

import com.pinHouse.server.platform.housing.complex.application.usecase.ComplexUseCase;
import com.pinHouse.server.platform.housing.complex.domain.entity.UnitType;
import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListResponse;
import com.pinHouse.server.platform.housing.notice.application.usecase.NoticeUseCase;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.like.application.dto.ComplexLikeResponse;
import com.pinHouse.server.platform.like.application.usecase.LikeQueryUseCase;
import com.pinHouse.server.platform.like.domain.Like;
import com.pinHouse.server.platform.like.domain.LikeRepository;
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

    private final LikeRepository repository;
    private final UserUseCase userService;
    /// 조회 의존성
    private final ComplexUseCase complexService;
    private final NoticeUseCase noticeService;

    /// 나의 좋아요 공고 목록 조회
    @Override
    @Transactional
    public List<NoticeListResponse> getNoticesLike(UUID userId) {

        /// 유저 조회
        User user = userService.loadUser(userId);

        /// 목록 조회 하기
        List<Like> likes = repository.findByUser_IdAndType(user.getId(), LikeType.NOTICE);

        /// 목록 조회한 아이디 한번에 조회하기
        List<String> noticesId = likes.stream()
                .map(Like::getTargetId)
                .toList();

        /// 한번에 조회하기 (N+1 방지)
        List<NoticeDocument> notices = noticeService.loadNotices(noticesId);

        /// DTO 변환 (좋아요 true 반환)
        return NoticeListResponse.from(notices);
    }

    /// 나의 좋아요 방 목록 조회
    @Override
    public List<ComplexLikeResponse> getComplexesLikes(UUID userId) {

        /// 유저 조회
        User user = userService.loadUser(userId);

        /// 방 목록 조회
        List<Like> likes = repository.findByUser_IdAndType(user.getId(), LikeType.ROOM);

        /// 목록 조회한 아이디 한번에 조회하기
        List<String> roomIds = likes.stream()
                .map(Like::getTargetId)
                .toList();

        /// 한번에 조회하기
        List<UnitType> rooms = complexService.loadRooms(roomIds);

        return null;
    }
}
