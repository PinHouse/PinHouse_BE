package com.pinHouse.server.platform.user.application.service;

import com.pinHouse.server.core.exception.code.UserErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.facility.domain.entity.FacilityType;
import com.pinHouse.server.platform.like.domain.LikeJpaRepository;
import com.pinHouse.server.platform.pinPoint.domain.repository.PinPointJpaRepository;
import com.pinHouse.server.platform.user.application.dto.*;
import com.pinHouse.server.platform.user.domain.entity.Gender;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenRequest;
import com.pinHouse.server.security.jwt.application.dto.JwtTokenResponse;
import com.pinHouse.server.security.jwt.application.util.JwtProvider;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.pinHouse.server.core.util.BirthDayUtil.parseBirthday;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserJpaRepository repository;

    /// 레디스
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    /// 삭제할 때
    private final PinPointJpaRepository pinPointRepository;
    private final LikeJpaRepository likeRepository;

    // =================
    //  퍼블릭 로직
    // =================

    /// 온보딩을 통한 유저 회원가입
    @Override
    @Transactional
    public JwtTokenResponse saveUser(String tempUserKey, UserRequest request) {

        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        if (raw instanceof TempUserInfo info) {

            /// 관심 목록과 함께, 값 저장하기
            User user = repository.save(createUser(info, request.facilityTypes()));

            /// 인증필터에 적용
            PrincipalDetails principalDetails = PrincipalDetails.of(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            /// 토큰 발급
            var tokenRequest = JwtTokenRequest.from(user);
            String accessToken = jwtProvider.createAccessToken(tokenRequest);
            String refreshToken = jwtProvider.createRefreshToken(tokenRequest);

            return JwtTokenResponse.of(accessToken, refreshToken);
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

        if (raw instanceof TempUserInfo info) {
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

    /// 삭제
    @Override
    @Transactional
    public void deleteUser(UUID userId) {

        /// 핀포인트 DB에서 삭제
        pinPointRepository.deleteByUser_Id(userId);

        /// 좋아요 삭제
        likeRepository.deleteByUser_Id(userId);

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
                .orElseThrow(() -> new CustomException(UserErrorCode.NOT_FOUND_USER));


    }


}
