package com.myhome.server.platform.domain.notice;

import com.myhome.server.platform.domain.BaseDomain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeReceptionCenter{

    private String location;

    private String phoneNumber;

    private String operationPeriod;

}
