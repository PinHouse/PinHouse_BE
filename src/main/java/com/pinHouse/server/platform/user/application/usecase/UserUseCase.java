package com.pinHouse.server.platform.user.application.usecase;

import com.pinHouse.server.platform.user.application.dto.*;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenResponse;

import java.util.Optional;
import java.util.UUID;

public interface UserUseCase {

    // =================
    //  퍼블릭 로직
    // =================

    /// 회원가입
    JwtTokenResponse saveUser(String tempUserKey, UserRequest request);

    /// 개인정보 조회하기
    MyPageResponse getMyPage(UUID userId);

    /// 타 유저 정보 조회하긴
    UserResponse getOtherUser(UUID otherUserId);

    /// 레디스에서 정보 가져오기
    TempUserResponse getUserByKey(String tempUserKey);

    /// 수정하기
    void updateUser(UpdateUserRequest request, UUID userId);

    /// 관심 시설 타입 수정하기
    void updateFacilityTypes(UpdateFacilityTypesRequest request, UUID userId);

    /// 탈퇴하기
    void deleteUser(UUID userId, WithdrawRequest request);

    // =================
    //  외부용 로직
    // =================

    /// DB 유저 조회
    User loadUser(UUID Id);

    /// DB 중복 유저 존재하는지 체크
    Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);
}
