package com.pinHouse.server.platform.housingFit.rule.application.service;

// --- 점수 계산기(가점제) ---
public record ScoreInput(int homelessYears, int familyCount, int accountYears) {
}
