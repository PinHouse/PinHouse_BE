package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    Optional<User> loadUserById(UUID Id);

    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);

    /// 레디스에서 정보 가져오기
    TempUserResponse getUserByKey(String tempUserKey);

    void saveUser(String tempUserKey, UserRequest request);

    boolean checkExistingById(UUID userId);

    /// 외부용 함수

    /// 개발용 유저 저장하기
    User saveUser(User user);

}
