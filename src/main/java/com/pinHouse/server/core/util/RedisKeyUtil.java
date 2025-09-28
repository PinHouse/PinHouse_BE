package com.pinHouse.server.core.util;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisKeyUtil {

    /// 개별 키 목록
    private static final String OAUTH2_TEMP_USER = "OAUTH2_TEMP_USER:";
    private static final String SEPARATOR = ":";
    private static final String LIST = "list";
    private static final String BEST = "best";
    private static final String AUDITORIUM = "auditorium";
    private static final String REVIEW = "review";
    private static final String RECENT_SEARCH_PREFIX = "recentSearch:guest:";
    private static final String SUMMARY = "Summary";

    /// 합쳐서 사용하는 키 목록
    private static final String BEST_AUDITORIUM_LIST_KEY = BEST + SEPARATOR + AUDITORIUM + SEPARATOR + LIST;
    private static final String BEST_REVIEW_LIST_KEY = BEST + SEPARATOR + REVIEW + SEPARATOR + LIST;
    private static final String REVIEW_SUMMARY_KEY = REVIEW + SUMMARY;

    /// 키 생성 함수
    public String generateOAuth2TempUserKey() {
        return OAUTH2_TEMP_USER + UUID.randomUUID();
    }
    public  String getGuestRecentSearchKey(String guestToken) { return RECENT_SEARCH_PREFIX + guestToken; }


}
