package co.kr.pinhouse.domain.user.application.usecase;

import java.util.Optional;
import java.util.UUID;

import co.kr.pinhouse.domain.user.application.dto.MyPageResponse;
import co.kr.pinhouse.domain.user.application.dto.TempUserResponse;
import co.kr.pinhouse.domain.user.application.dto.UpdateFacilityTypesRequest;
import co.kr.pinhouse.domain.user.application.dto.UpdateUserRequest;
import co.kr.pinhouse.domain.user.application.dto.UserRequest;
import co.kr.pinhouse.domain.user.application.dto.UserResponse;
import co.kr.pinhouse.domain.user.application.dto.WithdrawRequest;
import co.kr.pinhouse.domain.user.domain.entity.Provider;
import co.kr.pinhouse.domain.user.domain.entity.User;

public interface UserUseCase {

	// =================
	//  퍼블릭 로직
	// =================

	/// 회원가입 (JWT 토큰 생성은 security 모듈에서 처리)
	User saveUser(String tempUserKey, UserRequest request);

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
	User loadUser(UUID id);

	/// DB 중복 유저 존재하는지 체크
	Optional<User> loadUserBySocialAndSocialId(Provider socialType, String socialId);
}
