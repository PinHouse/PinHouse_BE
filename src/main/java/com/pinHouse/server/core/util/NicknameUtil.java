package com.pinHouse.server.core.util;

public class NicknameUtil {

    public static String generateFromSocial(String userName, String socialId) {
        String namePart = (userName == null || userName.isBlank()) ? "유저" : userName;
        String shortId = socialId.length() > 6 ? socialId.substring(0, 6) : socialId;
        return namePart + "_" + shortId;
    }

}
