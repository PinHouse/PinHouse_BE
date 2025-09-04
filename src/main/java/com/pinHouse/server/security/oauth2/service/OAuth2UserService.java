package com.pinHouse.server.security.oauth2.service;

import com.pinHouse.server.platform.user.application.usecase.UserPort;
import com.pinHouse.server.platform.user.domain.Gender;
import com.pinHouse.server.platform.user.domain.Provider;
import com.pinHouse.server.platform.user.domain.User;
import com.pinHouse.server.security.oauth2.domain.OAuth2UserInfo;
import com.pinHouse.server.security.oauth2.domain.PrincipalDetails;
import com.pinHouse.server.security.oauth2.domain.kakao.KakaoUserInfo;
import com.pinHouse.server.security.oauth2.domain.naver.NaverUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

import static com.pinHouse.server.core.util.BirthDayUtil.parseBirthday;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserPort userPort;

    /**
     * 소셜 로그인 유저 가져오기
     * @param userRequest   정보 요청
     * @return              유저
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        /// 유저 정보(attributes) 가져오기
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        /// resistrationId 가져오기
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        /// userNameAttributeName 가져오기
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        log.info(registrationId + ": " + userNameAttributeName);

        /// OAuth2를 바탕으로 정보 생성
        OAuth2UserInfo userInfo = null;

        /// Provider 에 따른 유저 생성
        userInfo = createOAuth2User(registrationId, oAuth2UserAttributes);

        Provider social = Provider.valueOf(userInfo.getProvider());
        Optional<User> existUser = userPort.loadUserBySocialAndSocialId(social, userInfo.getProviderId());

        /// 존재한다면 로그인
        if (existUser.isPresent()) {
            ///  이미 존재하는 유저를 반환한다.

            User user = existUser.get();
            log.info("이미 로그인한 유저입니다.");

            return PrincipalDetails.of(user, oAuth2UserAttributes);

        } else {
            ///  정보를 통해 임시 저장한 뒤, 개인정보 추가하도록 한다.

            User user = User.of(
                    Provider.valueOf(userInfo.getProvider()),
                    userInfo.getProviderId(),
                    userInfo.getUserName(),
                    userInfo.getEmail(),
                    userInfo.getImageUrl(),
                    null,
                    parseBirthday(userInfo.getBirthYear(), userInfo.getBirthday()),
                    Gender.getGender(userInfo.getGender())
            );

            User savedNewUser = userPort.saveUser(user);

            return PrincipalDetails.of(savedNewUser, oAuth2UserAttributes);
        }
    }


    /**
     * 소셜로그인 유저 생성
     * @param registrationId        소셜로그인 종류
     * @param oAuth2UserAttributes  소셜로그인 정보
     */
    private OAuth2UserInfo createOAuth2User(String registrationId, Map<String, Object> oAuth2UserAttributes) {
        OAuth2UserInfo userInfo;

        switch (registrationId.toUpperCase()) {
            case "KAKAO":
                userInfo = new KakaoUserInfo(oAuth2UserAttributes);
                break;
            case "NAVER":
                userInfo = new NaverUserInfo(oAuth2UserAttributes);
                break;
            default:
                throw new IllegalArgumentException("Unknown provider: " + registrationId);
        }

        return userInfo;
    }

}
