package co.kr.pinhouse.security.oauth2.domain.kakao;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import co.kr.pinhouse.security.oauth2.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;
	private final Map<String, Object> account;
	private final Map<String, Object> profile;

	@SuppressWarnings("unchecked")
	public KakaoUserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.account = Optional.ofNullable(attributes.get("kakao_account"))
			.filter(Map.class::isInstance)
			.map(m -> (Map<String, Object>)m)
			.orElse(Collections.emptyMap());

		this.profile = Optional.ofNullable(account.get("profile"))
			.filter(Map.class::isInstance)
			.map(m -> (Map<String, Object>)m)
			.orElse(Collections.emptyMap());
	}

	@Override
	public String getProvider() {
		return "KAKAO";
	}

	@Override
	public String getProviderId() {
		return Optional.ofNullable(attributes.get("id"))
			.map(Object::toString)
			.orElse("Unknown");
	}

	@Override
	public String getEmail() {
		return Optional.ofNullable(account.get("email"))
			.map(Object::toString)
			.orElse("No email provided");
	}

	@Override
	public String getUserName() {
		return Optional.ofNullable(profile.get("nickname"))
			.map(Object::toString)
			.orElse("No name provided");
	}

	@Override
	public String getImageUrl() {

		return Optional.ofNullable(profile.get("profile_image_url"))
			.map(Object::toString)
			.orElse("No image provided");
	}

	@Override
	public String getGender() {
		return "";
	}

	@Override
	public String getBirthday() {
		return "";
	}

	@Override
	public String getBirthYear() {
		return "";
	}
}
