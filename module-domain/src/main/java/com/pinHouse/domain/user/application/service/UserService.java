package com.pinHouse.domain.user.application.service;

import com.pinHouse.common.exception.code.UserErrorCode;
import com.pinHouse.common.response.CustomException;
import com.pinHouse.domain.diagnostic.diagnosis.domain.repository.DiagnosisJpaRepository;
import com.pinHouse.domain.housing.facility.domain.entity.FacilityType;
import com.pinHouse.domain.like.domain.LikeJpaRepository;
import com.pinHouse.domain.pinPoint.domain.repository.PinPointMongoRepository;
import com.pinHouse.domain.user.application.dto.*;
import com.pinHouse.domain.user.domain.entity.Gender;
import com.pinHouse.domain.user.domain.entity.Role;
import com.pinHouse.domain.user.domain.entity.User;
import com.pinHouse.domain.user.domain.repository.UserJpaRepository;
import com.pinHouse.domain.user.application.usecase.UserUseCase;
import com.pinHouse.domain.user.domain.entity.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pinHouse.common.util.BirthDayUtil.parseBirthday;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserJpaRepository repository;

    /// 레디스
    private final RedisTemplate<String, Object> redisTemplate;

    /// 삭제할 때
    private final PinPointMongoRepository pinPointRepository;
    private final LikeJpaRepository likeRepository;
    private final DiagnosisJpaRepository diagnosisRepository;

    // =================
    //  퍼블릭 로직
    // =================

    /// 온보딩을 통한 유저 회원가입 (JWT 생성은 security 모듈에서 처리)
    @Override
    @Transactional
    public User saveUser(String tempUserKey, UserRequest request) {
        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        if (raw == null) {
            throw new CustomException(UserErrorCode.NOT_TEMP_USER_KEY);
        }

        // TempUserInfo는 security 모듈에 있으므로 Map으로 처리 (임시)
        // app 모듈에서 security와 통합하여 처리 필요
        if (raw instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> infoMap = (Map<String, Object>) raw;

            // Map에서 필요한 정보 추출
            String socialType = (String) infoMap.get("socialType");
            String socialId = (String) infoMap.get("socialId");
            String email = (String) infoMap.get("email");
            String name = (String) infoMap.get("name");
            String profileImage = (String) infoMap.get("profileImageUrl");

            /// User 생성 및 저장
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .provider(Provider.valueOf(socialType))
                    .socialId(socialId)
                    .email(email)
                    .name(name)
                    .nickname(name)  // 닉네임은 이름으로 초기화
                    .profileImage(profileImage)
                    .role(Role.USER)
                    .facilityTypes(request.facilityTypes() != null ? request.facilityTypes() : new ArrayList<>())
                    .build();

            return repository.save(user);
        }

        /// 에러 발생
        throw new CustomException(UserErrorCode.BAD_REQUEST_ONBOARDING);
    }

    /// 레디스에 존재하는 데이터 조회
    @Override
    @Transactional(readOnly = true)
    public TempUserResponse getUserByKey(String tempUserKey) {

        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        /// 없다면 예외 처리
        if (raw == null){
            throw new CustomException(UserErrorCode.NOT_TEMP_USER_KEY);
        }

        if (raw instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> info = (Map<String, Object>) raw;
            /// 리턴
            return TempUserResponse.from(info);
        } else {
            throw new CustomException(UserErrorCode.BAD_REQUEST_REDIS);
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

    /// 관심 시설 타입 수정
    @Override
    @Transactional
    public void updateFacilityTypes(UpdateFacilityTypesRequest request, UUID userId) {

        /// 트랜잭션 (영속성 컨테이너 불러와서 더티체킹)
        User user = loadUserWithFacilityType(userId);

        /// 더티체킹
        user.updateFacilityTypes(request.facilityTypes());

    }

    /// 삭제
    @Override
    @Transactional
    public void deleteUser(UUID userId, WithdrawRequest request) {

        /// 탈퇴 사유 로깅 (0개 이상 복수 선택 가능)
        if (request.reasons() != null && !request.reasons().isEmpty()) {
            log.info("회원 탈퇴 - userId={}, 탈퇴 사유 개수={}, 사유={}",
                    userId,
                    request.reasons().size(),
                    request.reasons().stream()
                            .map(reason -> reason.getValue())
                            .toList());
        } else {
            log.info("회원 탈퇴 - userId={}, 탈퇴 사유 선택 안함", userId);
        }

        /// 핀포인트 DB에서 삭제
        pinPointRepository.deleteByUserId(userId.toString());

        /// 좋아요 삭제
        likeRepository.deleteByUser_Id(userId);

        /// 진단 삭제
        diagnosisRepository.deleteByUser_Id(userId);

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
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));
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
    // createUser 메서드는 제거됨 (saveUser에서 직접 처리)

    protected User loadUserWithFacilityType(UUID userId) {
        return repository.findWithFacilityTypesById(userId)
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));


    }


}
