package com.pinHouse.server.platform.like.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndTargetIdAndType(UUID userId, String targetId, LikeType type);

}
