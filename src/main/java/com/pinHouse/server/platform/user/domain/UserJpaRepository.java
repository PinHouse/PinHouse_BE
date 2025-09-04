package com.pinHouse.server.platform.user.domain;

import com.pinHouse.server.platform.user.domain.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);

    Optional<UserJpaEntity> findById(UUID id);


    Optional<UserJpaEntity> findByProviderAndSocialId(Provider social, String socialId);

}
