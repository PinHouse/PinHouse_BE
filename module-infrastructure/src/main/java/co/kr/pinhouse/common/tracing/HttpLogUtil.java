package co.kr.pinhouse.common.tracing;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;
import static co.kr.pinhouse.common.util.LogSanitizer.sanitizeName;

import org.springframework.stereotype.Component;

import co.kr.pinhouse.common.util.HttpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpLogUtil {

	private final HttpUtil httpUtil;

	/// HTTP 대한 로그를 찍는 것
	public void logHttpRequest(HttpServletRequest httpServletRequest, String type) {

		/// 바디
		var body = httpServletRequest.getContentType();

		/// 헤더에서 요청 정보 가져오기
		var clientInfo = httpUtil.getClientInfo(httpServletRequest);

		/// 로그
		log.info("{} : {}, [{}], {}, {} ,{}", sanitize(type), sanitize(clientInfo.ip()),
			sanitize(clientInfo.httpMethod()), sanitize(clientInfo.uri()), sanitizeName(clientInfo.userName()),
			sanitize(body));

	}

}
