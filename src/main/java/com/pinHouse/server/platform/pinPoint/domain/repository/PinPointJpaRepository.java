package com.pinHouse.server.platform.pinPoint.domain.repository;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PinPointJpaRepository extends JpaRepository<PinPoint, Long> {

    /// 유저에 따른 목록 조회
    List<PinPoint> findByUser(User user);

    /// 아이디와 유저ID에 따른 존재 조회
    Optional<PinPoint> findByIdAndUser_Id(Long id, UUID userId);

    /// 아이디와 유저ID에 따른 존재 여부 조회
    boolean existsByIdAndUser(Long id, User user);

}
