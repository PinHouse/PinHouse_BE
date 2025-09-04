package com.pinHouse.server.platform.notice.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeCount {

    private String noticeId;

    private int libraryCount;

    private int animalCount;

    private int parkCount;

    private int sportCount;

    private int walkCount;

    /// 정적 팩토리 메서드
    public NoticeCount of(String noticeId, int libraryCount, int animalCount, int parkCount, int sportCount) {
        return NoticeCount.builder()
                .noticeId(noticeId)
                .libraryCount(libraryCount)
                .animalCount(animalCount)
                .parkCount(parkCount)
                .sportCount(sportCount)
                .build();
    }
}
