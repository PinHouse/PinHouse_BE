package com.pinHouse.server.platform.diagnostic.diagnosis.domain.repository;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Long> {

    /**
     * 유저 기반으로 진단 탐색하기
     * @param user  유저
     */
    Diagnosis findByUser(User user);

    /**
     * 유저 기반으로 모든 진단 히스토리 조회 (최신순)
     * @param user  유저
     * @return 진단 목록
     */
    List<Diagnosis> findAllByUserOrderByCreatedAtDesc(User user);

    /**
     * 유저 ID 기반으로 진단 삭제
     * @param userId 유저 ID
     */
    void deleteByUser_Id(UUID userId);

}
