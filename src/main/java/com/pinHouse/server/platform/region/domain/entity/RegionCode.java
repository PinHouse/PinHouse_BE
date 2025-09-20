package com.pinHouse.server.platform.region.domain.entity;

public enum RegionCode {
    SEOUL, INCHEON, GYEONGGI, BUSAN,
    DAEGU, GWANGJU, DAEJEON, ULSAN,
    NON_SUDO; // 기타 지역

    public static RegionCode from(String s) {
        switch (s.toUpperCase()) {
            case "SEOUL": return SEOUL;
            case "INCHEON": return INCHEON;
            case "GYEONGGI": return GYEONGGI;
            case "BUSAN": return BUSAN;
            case "DAEGU": return DAEGU;
            case "GWANGJU": return GWANGJU;
            case "DAEJEON": return DAEJEON;
            case "ULSAN": return ULSAN;
            default: return NON_SUDO;
        }
    }

    public boolean isSudo() {
        return this == SEOUL || this == INCHEON || this == GYEONGGI || this == BUSAN;
    }

    public boolean isMetro() {
        return this == DAEGU || this == GWANGJU || this == DAEJEON || this == ULSAN;
    }
}

