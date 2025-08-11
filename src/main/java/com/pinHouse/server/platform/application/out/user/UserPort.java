package com.pinHouse.server.platform.application.out.user;

import com.pinHouse.server.platform.domain.user.Provider;
import com.pinHouse.server.platform.domain.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPort {

    Optional<User> loadUserById(UUID Id);

    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);

    User saveUser(User user);

    boolean existsByEmail(String email);

    boolean checkExistingById(UUID userId);
}
