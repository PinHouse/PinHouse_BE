package com.pinHouse.server.platform.pinPoint.domain.repository;

import com.pinHouse.server.platform.pinPoint.domain.entity.PinPoint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PinPointMongoRepository extends MongoRepository<PinPoint, String> {

    /// 유저에 따른 목록 조회
    List<PinPoint> findByUserId(String userId);

    /// 아이디와 유저ID에 따른 존재 조회
    Optional<PinPoint> findByIdAndUserId(String id, String userId);

    /// 아이디와 유저ID에 따른 존재 여부 조회
    boolean existsByIdAndUserId(String id, String userId);

    void deleteByUserId(String userId);
}
