package com.pinHouse.server.platform.housing.facility.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.pinHouse.server.core.exception.code.FacilityErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import lombok.RequiredArgsConstructor;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public enum FacilityType {

    LIBRARY("도서관", Set.of("도서관", "문화센터")),
    PARK("공원", Set.of("공원", "문화센터")),
    ANIMAL("동물 관련시설", Set.of("동물 관련시설", "문화센터")),
    EXHIBITION("전시회", Set.of("전시회", "문화센터")),

    WALKING("산책길", Set.of("산책길", "산책로")),
    SPORT("실내 액티비티", Set.of("실내 액티비티", "스포츠 시설")),
    STORE("대형마트-백화점", Set.of("대형마트-백화점", "대형점포")),
    HOSPITAL("병원-약국", Set.of("병원-약국", "병원")),
    LAUNDRY("세탁소", Set.of("세탁소", "빨래방")),
    CULTURE_CENTER("문화센터", Set.of("문화센터"));

    private final String value;
    private final Set<String> aliases;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static FacilityType fromValue(String value) {
        if (value == null) {
            throw new CustomException(FacilityErrorCode.BAD_REQUEST_INPUT_FACILITY);
        }

        String normalized = normalize(value);

        if (CULTURE_CENTER.matches(normalized) ||
                FacilityType.cultureCenterMembers().stream().anyMatch(t -> t.matches(normalized))) {
            return CULTURE_CENTER;
        }

        for (FacilityType facilityType : FacilityType.values()) {
            if (facilityType == CULTURE_CENTER) continue;
            if (facilityType.matches(normalized)) {
                return facilityType;
            }
        }

        try {
            return FacilityType.valueOf(normalized.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignore) { }

        throw new CustomException(FacilityErrorCode.BAD_REQUEST_INPUT_FACILITY);
    }

    public FacilityType displayType() {
        return isCultureCenterMember(this) ? CULTURE_CENTER : this;
    }

    private boolean matches(String source) {
        String normalized = normalize(source);
        return aliases.stream().anyMatch(alias -> normalize(alias).equals(normalized))
                || normalize(getValue()).equals(normalized);
    }

    private static String normalize(String input) {
        return input == null ? "" : input.trim().replaceAll("\\s+", "");
    }

    public static boolean isCultureCenterMember(FacilityType type) {
        return CULTURE_TYPES.contains(type);
    }

    public static Set<FacilityType> cultureCenterMembers() {
        return CULTURE_TYPES;
    }

    private static final Set<FacilityType> CULTURE_TYPES = EnumSet.of(LIBRARY, PARK, ANIMAL, EXHIBITION);

}
