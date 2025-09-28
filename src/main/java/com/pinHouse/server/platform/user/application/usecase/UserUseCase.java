package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.application.dto.request.UserRequest;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.security.oauth2.domain.TempUserInfo;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    Optional<User> loadUserById(UUID Id);

    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);

    User saveUser(User user);

    void saveUser(UserRequest request);

    boolean existsByEmail(String email);

    boolean checkExistingById(UUID userId);

    /// 레디스에서 정보 가져오기
    TempUserInfo getUserByKey(String key);

}
