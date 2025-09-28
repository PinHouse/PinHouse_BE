package com.pinHouse.server.platform.user.application.service;

import com.pinHouse.server.platform.user.application.dto.request.UserRequest;
import com.pinHouse.server.platform.user.domain.entity.User;
import com.pinHouse.server.platform.user.domain.repository.UserJpaRepository;
import com.pinHouse.server.platform.user.application.usecase.UserUseCase;
import com.pinHouse.server.platform.user.domain.entity.Provider;
import com.pinHouse.server.security.oauth2.domain.TempUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService implements UserUseCase {

    private final UserJpaRepository repository;

    /// 레디스
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    /**
     * 온보딩을 통한 유저 회원가입
     * @param request   회원가입
     */
    @Override
    public void saveUser(UserRequest request) {

    }

    /**
     * 이메일이 존재하는지 체크
     * @param userId    유저ID
     */
    @Override
    public boolean checkExistingById(UUID userId) {
        return repository.existsById(userId);
    }

    @Override
    public Optional<User> loadUserById(UUID userId) {
        return repository.findById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        Optional<User> userJpaEntity = repository.findByEmail(email);
        return userJpaEntity.isPresent();
    }

    @Override
    public Optional<User> loadUserBySocialAndSocialId(Provider social, String socialId) {
        return repository.findByProviderAndSocialId(social, socialId);
    }


    /// 최초 회원가입에서 사용하는 함수
    @Override
    public TempUserInfo getUserByKey(String tempUserKey) {

        /// 값 가져오기
        Object raw = redisTemplate.opsForValue().get(tempUserKey);

        /// 없다면 예외 처리
        if (raw == null){
            throw new IllegalStateException("TempUserInfo 복원 실패");
        }

        try {
            if (raw instanceof TempUserInfo info) {
                /// 레디스에서 삭제
                redisTemplate.delete(tempUserKey);
                /// 리턴
                return info;
            }

            throw new IllegalStateException("지원하지 않는 Redis 값 타입: " + raw.getClass());
        } catch (Exception e) {
            throw new IllegalStateException("TempUserInfo 복원 실패", e);
        }
    }



}
