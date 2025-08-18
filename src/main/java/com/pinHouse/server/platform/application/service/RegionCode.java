package com.pinHouse.server.platform.application.service;

public enum RegionCode {
    SUDO, NON_SUDO;

    public static RegionCode from(String s) {
        return "SUDO".equalsIgnoreCase(s) ? SUDO : NON_SUDO;
    }
}
