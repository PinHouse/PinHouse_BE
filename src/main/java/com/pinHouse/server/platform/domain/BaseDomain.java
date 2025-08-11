package com.pinHouse.server.platform.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class BaseDomain {

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
