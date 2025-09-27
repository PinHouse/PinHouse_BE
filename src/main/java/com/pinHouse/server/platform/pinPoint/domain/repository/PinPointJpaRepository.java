package com.pinHouse.server.platform.pinPoint.domain.repository;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import com.pinHouse.server.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PinPointJpaRepository extends JpaRepository<PinPoint, Long> {
    boolean existsByIdAndUser(Long id, User user);

    Long id(Long id);

    List<PinPoint> findByUser(User user);
}
