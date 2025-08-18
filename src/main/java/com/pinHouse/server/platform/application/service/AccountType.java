package com.pinHouse.server.platform.application.service;

public enum AccountType {
    SAVING, DEPOSIT, INSTALLMENT;

    public static AccountType from(String s) {
        if (s == null) return SAVING;
        return switch (s.toUpperCase()) {
            case "DEPOSIT" -> DEPOSIT;
            case "INSTALLMENT" -> INSTALLMENT;
            default -> SAVING;
        };
    }
}

