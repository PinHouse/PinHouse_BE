package com.pinHouse.server.platform.user.application.service;

import com.pinHouse.server.core.response.response.ErrorCode;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.user.application.dto.*;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pinHouse.server.core.util.BirthDayUtil.parseBirthday;

@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserJpaRepository repository;

    /// 레디스
    private final RedisTemplate<String, Object> redisTemplate;

    // =================
    //  퍼블릭 로직
    // =================

    /// 온보딩을 통한 유저 회원가입
    @Override
    @Transactional
    public void saveUser(String tempUserKey, UserRequest request) {

        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        if (raw instanceof TempUserInfo info) {

            /// 관심 목록과 함께, 값 저장하기
            repository.save(createUser(info, request.facilityTypes()));
        }
    }

    /// 레디스에 존재하는 데이터 조회
    @Override
    @Transactional(readOnly = true)
    public TempUserResponse getUserByKey(String tempUserKey) {

        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        /// 없다면 예외 처리
        if (raw == null){
            throw new IllegalStateException("TempUserInfo 복원 실패");
        }

        try {
            if (raw instanceof TempUserInfo info) {
                /// 리턴
                return TempUserResponse.from(info);
            }

            throw new IllegalStateException("지원하지 않는 Redis 값 타입: " + raw.getClass());
        } catch (Exception e) {
            throw new IllegalStateException("TempUserInfo 복원 실패", e);
        }
    }

    /// 수정
    @Override
    @Transactional
    public void updateUser(UpdateUserRequest request, UUID userId) {

        /// 트랜잭션 (영속성 컨테이너 불러와서 더티체킹)
        User user = loadUser(userId);

        /// 더티체킹
        user.update(request.imageUrl(), request.nickname());

    }

    /// 삭제
    @Override
    @Transactional
    public void deleteUser(UUID userId) {

        /// DB에서 삭제
        repository.deleteById(userId);
    }


    /// 나의 정보 조회
    @Override
    @Transactional(readOnly = true)
    public MyPageResponse getMyPage(UUID userId) {

        /// 유저 정보 조회
        User user = loadUserWithFacilityType(userId);

        /// 리턴
        return MyPageResponse.from(user);
    }



    /// 타인의 유저 정보 조회
    @Override
    @Transactional(readOnly = true)
    public UserResponse getOtherUser(UUID otherUserId) {

        /// 유저 정보 조회
        User user = loadUser(otherUserId);

        /// 리턴
        return UserResponse.from(user);
    }

    // =================
    //  외부 로직
    // =================
    /// ID 기반 조회
    @Transactional(readOnly = true)
    public User loadUser(UUID userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    /// 소셜로그인 중복 로그인 조횐
    @Override
    @Transactional(readOnly = true)
    public Optional<User> loadUserBySocialAndSocialId(Provider social, String socialId) {
        return repository.findByProviderAndSocialId(social, socialId);
    }

    // =================
    //  내부 로직
    // =================
    /// 내부함수 유저 생성
    private User createUser(TempUserInfo userInfo, List<FacilityType> facilityTypeList) {

        return User.of(
                Provider.valueOf(userInfo.getSocial()),
                userInfo.getSocialId(),
                userInfo.getUsername(),
                userInfo.getEmail(),
                userInfo.getImageUrl(),
                null,
                parseBirthday(userInfo.getBirthyear(), userInfo.getBirthday()),
                Gender.getGender(userInfo.getGender()),
                facilityTypeList
        );
    }

    protected User loadUserWithFacilityType(UUID userId) {
        return repository.findWithFacilityTypesById(userId)
                .orElseThrow(() -> new NoSuchElementException(ErrorCode.USER_NOT_FOUND.getMessage()));


    }


}
