package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.domain.Provider;
import com.pinHouse.server.platform.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPort {

    Optional<User> loadUserById(UUID Id);

    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);

    User saveUser(User user);

    boolean existsByEmail(String email);

    boolean checkExistingById(UUID userId);
}
