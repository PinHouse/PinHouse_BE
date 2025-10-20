package com.pinHouse.server.core.logging;

import com.pinHouse.server.core.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpLogUtil {

    private final HttpUtil httpUtil;

    /// HTTP 대한 로그를 찍는 것
    public void logHttpRequest(HttpServletRequest httpServletRequest, String type) {

        /// 로그를 위한 함수
        String ipAddress = httpUtil.getClientIp(httpServletRequest);

        /// 헤더에서 요청 정보 가져오기
        var clientInfo = httpUtil.getClientInfo(httpServletRequest);

        /// 로그
        log.info("{} : {}, [{}], {}, {}", type, ipAddress, clientInfo.httpMethod(), clientInfo.uri(), clientInfo.userName());

    }

}
