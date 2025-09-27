package com.pinHouse.server.platform.pinPoint.application.dto.request;

import lombok.Getter;

@Getter
public class PinPointRequest {

    private String address;

    private String name;

    /// 이것을 제일 우선 순위로 설정할지
    private boolean first;

}
