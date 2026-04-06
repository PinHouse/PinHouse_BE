package com.pinHouse.domain.user.application.service;

import java.util.*;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pinHouse.common.dto.TempUserInfo;
import com.pinHouse.common.exception.code.UserErrorCode;
import com.pinHouse.common.response.CustomException;
import com.pinHouse.domain.diagnostic.diagnosis.domain.repository.DiagnosisJpaRepository;
import com.pinHouse.domain.like.domain.LikeJpaRepository;
import com.pinHouse.domain.pinPoint.domain.repository.PinPointMongoRepository;
import com.pinHouse.domain.user.application.dto.*;
import com.pinHouse.domain.user.application.usecase.UserUseCase;
import com.pinHouse.domain.user.domain.entity.Gender;
import com.pinHouse.domain.user.domain.entity.Provider;
import com.pinHouse.domain.user.domain.entity.Role;
import com.pinHouse.domain.user.domain.entity.User;
import com.pinHouse.domain.user.domain.repository.UserJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

		// TempUserInfo 타입으로 처리
		TempUserInfo userInfo;

		if (raw instanceof TempUserInfo) {
			userInfo = (TempUserInfo) raw;
		} else if (raw instanceof Map) {
			// 하위 호환성을 위해 Map도 지원
			@SuppressWarnings("unchecked")
			Map<String, Object> infoMap = (Map<String, Object>) raw;
			userInfo = TempUserInfo.builder()
					.social((String) infoMap.get("social"))
					.socialId((String) infoMap.get("socialId"))
					.email((String) infoMap.get("email"))
					.username((String) infoMap.get("username"))
					.imageUrl((String) infoMap.get("imageUrl"))
					.gender((String) infoMap.get("gender"))
					.birthyear((String) infoMap.get("birthyear"))
					.birthday((String) infoMap.get("birthday"))
					.build();
		} else {
			log.error("Redis raw object type is unexpected: {}", raw.getClass().getName());
			throw new CustomException(UserErrorCode.BAD_REQUEST_ONBOARDING);
		}

		log.info("Processing user signup - social: {}, email: {}, name: {}",
				userInfo.getSocial(), userInfo.getEmail(), userInfo.getUsername());

		Provider provider = Provider.valueOf(userInfo.getSocial());

		Optional<User> existUser = repository.findByProviderAndSocialId(provider, userInfo.getSocialId());
		if (existUser.isPresent()) {
			redisTemplate.delete(tempUserKey);
			return existUser.get();
		}

		// Gender 파싱 (문자열 -> Gender enum)
		Gender gender = Gender.Other; // 기본값
		if (userInfo.getGender() != null) {
			try {
				// "Male", "Female", "Other" 등의 enum 이름으로 시도
				gender = Gender.valueOf(userInfo.getGender());
			} catch (IllegalArgumentException e) {
				// "남성", "여성", "미정" 등의 값으로 변환 시도
				gender = Gender.getGender(userInfo.getGender());
			}
		}

		/// User 생성 및 저장
		User user = User.builder()
				.id(UUID.randomUUID())
				.provider(provider)
				.socialId(userInfo.getSocialId())
				.email(userInfo.getEmail())
				.name(userInfo.getUsername())
				.nickname(userInfo.getUsername())  // 닉네임은 이름으로 초기화
				.profileImage(userInfo.getImageUrl())
				.gender(gender)
				.role(Role.USER)
				.facilityTypes(request.facilityTypes() != null ? request.facilityTypes() : new ArrayList<>())
				.build();

		User savedUser = repository.save(user);
		redisTemplate.delete(tempUserKey);

		return savedUser;
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

		if (raw instanceof TempUserInfo) {
			return TempUserResponse.from((TempUserInfo) raw);
		} else if (raw instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> info = (Map<String, Object>) raw;
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
