package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    Optional<User> loadUserById(UUID Id);

    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);

    User saveUser(User user);

    boolean existsByEmail(String email);

    boolean checkExistingById(UUID userId);
}
