package com.pinHouse.server.platform.housing.notice.application.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

/// 공고 목록 조회를 위한 요청 DTO
@Schema(name = "[요청][공고] 공고 목록 조회 Request", description = "공고 목록 조회를 위한 요청 DTO입니다.")
public record NoticeListRequest(

        @Schema(description = "지역 유형 목록", example = "[\"서울특별시\", \"경기도\"]")
        List<Region> regionType,

        @Schema(description = "대상 유형 목록", example = "[\"청년\", \"신혼부부\"]")
        List<TargetType> targetType,

        @Schema(description = "임대 유형 목록", example = "[\"국민임대\", \"행복주택\"]")
        List<LeaseType> leaseType,

        @Schema(description = "주택 유형 목록", example = "[\"아파트\", \"오피스텔\"]")
        List<HouseType> houseTypes,

        @Schema(description = "공고 마감 여부", example = "모집중")
        NoticeStatus status,

        @Schema(description = "정렬 유형", example = "최신공고순")
        ListSortType sortType

) {

    @Getter
    @RequiredArgsConstructor
    public enum Region {
        // 수도권
        SEOUL("서울특별시"),
        INCHEON("인천광역시"),
        GYEONGGI("경기도"),

        // 충청권
        SEJONG("세종특별자치도"),
        DAEJEON("대전광역시"),
        CHUNGBUK("충청북도"),
        CHUNGNAM("충청남도"),

        // 호남권
        GWANGJU("광주광역시"),
        JEONBUK("전북특별자치도"),
        JEONNAM("전라남도"),

        // 영남권
        BUSAN("부산광역시"),
        DAEGU("대구광역시"),
        ULSAN("울산광역시"),
        GYEONGBUK("경상북도"),
        GYEONGNAM("경상남도"),

        // 강원·제주권
        JEJU("제주특별자치도"),
        GANGWON("강원특별자치도");

        private final String fullName;

        @JsonValue
        public String getFullName() {
            return fullName;
        }

        @JsonCreator
        public static Region fromFullName(String fullName) {
            for (Region region : values()) {
                if (region.fullName.equals(fullName)) {
                    return region;
                }
            }
            throw new IllegalArgumentException("Unknown region name: " + fullName);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum LeaseType {
        // 공공 임대
        PUBLIC_INTEGRATED("통합공공임대"),
        PERMANENT("영구임대"),
        NATIONAL("국민임대"),
        HAPPY_HOUSE("행복주택"),

        // 민간 임대
        PRIVATE_PUBLIC_SUPPORTED("공공지원민간임대주택"),
        PURCHASED("매입임대"),
        LEASE_5_YEARS("5년임대"),
        LEASE_6_YEARS("6년임대"),
        LEASE_10_YEARS("10년임대"),
        LEASE_50_YEARS("50년임대"),

        // 전세형 임대
        LONG_TERM_JEONSE("장기전세"),
        JEONSE("전세임대");

        private final String displayName;

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }

        @JsonCreator
        public static LeaseType fromDisplayName(String name) {
            for (LeaseType type : values()) {
                if (type.displayName.equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown lease type: " + name);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum HouseType {
        APARTMENT("아파트"),
        OFFICETEL("오피스텔"),
        DORMITORY("기숙사"),
        MULTI_FAMILY("다세대주택"),
        ROW_HOUSE("다가구주택"),
        TOWNHOUSE("연립주택"),
        DETACHED_HOUSE("단독주택");

        private final String displayName;

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }

        @JsonCreator
        public static HouseType fromDisplayName(String name) {
            for (HouseType type : values()) {
                if (type.displayName.equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown house type: " + name);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum TargetType {

        // 청년층
        YOUTH("청년"),
        UNIVERSITY_STUDENT("대학생"),
        JOB_SEEKER("취업준비생"),

        // 가족형
        NEWLYWED("신혼부부"),
        NEWBORN("신생아"),

        // 주거취약계층
        SENIOR("고령자(노인)"),
        DISABLED("장애인"),
        SINGLE_PARENT("한부모"),
        PROTECTED_YOUTH("자립준비청년(보호종료아동)"),
        MULTI_CHILD("다자녀"),
        EMERGENCY_HOUSING_SUPPORT("긴급 주거지원대상자"),
        HOUSING_BENEFICIARY("주거급여수급자"),
        LOW_INCOME("저소득층"),
        ARTIST("예술인"),
        NATIONAL_MERIT("국가유공자"),
        OTHER("기타"),

        // 주택보유 상태
        HOMELESS("무주택자"),
        HOMELESS_HOUSEHOLD("무주택가구"),
        HOMEOWNER("유주택자"),

        // 소득
        INCOME("소득있음"),
        NO_INCOME("소득없음"),

        // 성별
        MALE("남"),
        FEMALE("여");

        private final String displayName;

        @JsonValue
        public String getDisplayName() {
            return displayName;
        }

        @JsonCreator
        public static TargetType fromDisplayName(String name) {
            for (TargetType type : values()) {
                if (type.displayName.equals(name)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown target type: " + name);
        }
    }

    // =================
    //  ListSortType 퍼블릭 로직
    // =================

    /// 목록 조회를 위한 정렬 파라미터
    @RequiredArgsConstructor
    public enum ListSortType {

        LATEST("최신공고순"),
        END("마감임박순");

        private final String label;

        @JsonValue
        public String getLabel() {
            return label;
        }

        public static ListSortType from(String source) {
            if (source == null) return null;
            String s = normalize(source);

            /// Enum.name() 매칭 허용 (LATEST, DEADLINE_ASC ...)
            for (ListSortType t : values()) {
                if (t.name().equalsIgnoreCase(s)) return t;
            }
            /// 한글 라벨 매칭 허용
            for (ListSortType t : values()) {
                if (normalize(t.label).equalsIgnoreCase(s)) return t;
            }
            throw new IllegalArgumentException("Invalid ListSortType: " + source);
        }
    }


    // =================
    //  모집대상 로직
    // =================

    /// 상세 조회를 위한 정렬 파라미터
    @RequiredArgsConstructor
    public enum NoticeStatus {

        ALL("전체"),
        RECRUITING("모집중");

        private final String label;

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static NoticeStatus fromLabel(String label) {
            if (label == null) {
                return null;
            }
            for (NoticeStatus status : values()) {
                if (status.label.equals(label)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown NoticeStatus label: " + label);
        }
    }

    // =================
    //  내부 로직
    // =================
    private static String normalize(String x) {
        return x.trim().replaceAll("\\s+", "");
    }





}


