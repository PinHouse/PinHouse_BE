package com.pinHouse.server.platform.user.application.service;

import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserJpaRepository repository;

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public boolean checkExistingById(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    public Optional<User> loadUserById(UUID userId) {
        return repository.findById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<User> userJpaEntity = repository.findByEmail(email);
        return userJpaEntity.isPresent();
    }

    @Override
    public Optional<User> loadUserBySocialAndSocialId(Provider social, String socialId) {
        return repository.findByProviderAndSocialId(social, socialId);
    }
}
