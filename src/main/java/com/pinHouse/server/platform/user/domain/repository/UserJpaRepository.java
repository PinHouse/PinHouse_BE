package com.pinHouse.server.platform.user.domain.repository;

import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByProviderAndSocialId(Provider social, String socialId);

    @EntityGraph(attributePaths = "facilityTypes") // LAZY 컬렉션을 같이 로딩
    Optional<User> findWithFacilityTypesById(UUID id);
}
