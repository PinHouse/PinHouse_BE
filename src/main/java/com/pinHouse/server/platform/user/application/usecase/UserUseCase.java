package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.application.dto.UserRequest;
import com.pinHouse.server.platform.user.application.dto.TempUserResponse;
import com.pinHouse.server.platform.user.application.dto.MyPageResponse;
import com.pinHouse.server.platform.user.application.dto.UserResponse;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {


    /// 회원가입
    void saveUser(String tempUserKey, UserRequest request);

    /// 개인정보 조회하기
    MyPageResponse getMyPage(UUID userId);

    /// 타 유저 정보 조회하긴
    UserResponse getOtherUser(UUID otherUserId);

    /// 레디스에서 정보 가져오기
    TempUserResponse getUserByKey(String tempUserKey);

    /// 외부용 함수
    User loadUser(UUID Id);

    /// 중복 유저 존재하는지 체크
    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);
}
