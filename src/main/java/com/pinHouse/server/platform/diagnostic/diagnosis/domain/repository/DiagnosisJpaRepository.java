package com.pinHouse.server.platform.diagnostic.diagnosis.domain.repository;

import com.pinHouse.server.platform.diagnostic.diagnosis.domain.entity.Diagnosis;
import com.pinHouse.server.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Long> {

    /**
     * 유저 기반으로 진단 탐색하기
     * @param user  유저
     */
    Diagnosis findByUser(User user);

}
