package com.pinHouse.server.platform.adapter.out;

import com.pinHouse.server.platform.adapter.out.jpa.user.UserJpaEntity;
import com.pinHouse.server.platform.adapter.out.jpa.user.UserJpaRepository;
import com.pinHouse.server.platform.application.out.user.UserPort;
import com.pinHouse.server.platform.domain.user.Provider;
import com.pinHouse.server.platform.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserJpaAdapter implements UserPort {

    private final UserJpaRepository repository;

    @Override
    public User saveUser(User user) {
        var entity = UserJpaEntity.from(user);

        return repository.save(entity)
                .toDomain();
    }

    @Override
    public boolean checkExistingById(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    public Optional<User> loadUserById(UUID userId) {
        return repository.findById(userId)
                .map(UserJpaEntity::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<UserJpaEntity> userJpaEntity = repository.findByEmail(email);
        return userJpaEntity.isPresent();
    }

    @Override
    public Optional<User> loadUserBySocialAndSocialId(Provider social, String socialId) {
        return repository.findByProviderAndSocialId(social, socialId)
                .map(UserJpaEntity::toDomain);
    }
}
