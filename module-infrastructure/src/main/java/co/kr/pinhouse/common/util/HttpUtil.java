package co.kr.pinhouse.common.util;

import static co.kr.pinhouse.common.util.KeyUtil.ACCESS_TOKEN;
import static co.kr.pinhouse.common.util.KeyUtil.REFRESH_TOKEN;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class HttpUtil {

	private static final String BEARER_PREFIX = "Bearer ";
	private static final String REFRESH_TOKEN_HEADER = "X-Refresh-Token";

	@Value("${auth.jwt.access.expiration}")
	private long accessExpiration;

	@Value("${auth.jwt.access.dev_expiration}")
	private long devExpiration;

	@Value("${auth.jwt.refresh.expiration}")
	private long refreshExpiration;

	@Value("${auth.cookie.secureOption}")
	private boolean secureOption;

	@Value("${auth.cookie.sameSiteOption}")
	private String sameSiteOption;

	@Value("${auth.cookie.PathOption}")
	private String cookiePathOption;

	/// 액세스 토큰 쿠키 가져오기
	public Optional<String> getAccessToken(HttpServletRequest request) {
		return extractBearerAuthorizationToken(request)
			.or(() -> extractCookieToken(request, ACCESS_TOKEN));
	}

	/// 리프레쉬 토큰 쿠키 가져오기
	public Optional<String> getRefreshToken(HttpServletRequest request) {
		return extractHeaderToken(request, REFRESH_TOKEN_HEADER)
			.or(() -> extractCookieToken(request, REFRESH_TOKEN));
	}

	/// 액세스 토큰을 쿠키에 저장하기
	public void addAccessTokenCookie(HttpServletResponse httpServletResponse, String accessToken) {

		/// 쿠키 생성 및 저장
		createCookie(httpServletResponse, ACCESS_TOKEN, accessToken, accessExpiration);

	}

	/// 개발용 액세스 토큰을 쿠키에 저장하기
	public void addDevAccessTokenCookie(HttpServletResponse httpServletResponse, String devAccessToken) {

		/// 쿠키 생성 및 저장
		createCookie(httpServletResponse, ACCESS_TOKEN, devAccessToken, devExpiration);

	}

	/// 리프레쉬 토큰을 쿠키에 저장하기
	public void addRefreshTokenCookie(HttpServletResponse httpServletResponse, String refreshToken) {

		/// 쿠키 생성 및 저장
		createCookie(httpServletResponse, REFRESH_TOKEN, refreshToken, refreshExpiration);
	}

	/// 액세스 토큰을 삭제하기
	public void removeAccessTokenCookie(HttpServletResponse httpServletResponse) {
		deleteCookie(httpServletResponse, ACCESS_TOKEN);
	}

	/// 리프레쉬 토큰을 삭제하기
	public void removeRefreshTokenCookie(HttpServletResponse httpServletResponse) {
		deleteCookie(httpServletResponse, REFRESH_TOKEN);
	}

	/// 요청자의 정보를 헤더에서 조회하기 위한 함수
	public HeaderInfo getClientInfo(HttpServletRequest request) {

		/// IP
		String ip = getClientIp(request);

		/// 메서드
		String httpMethod = request.getMethod();

		/// 요청 주소
		String uri = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

		/// 요청자
		String username = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "익명";

		return new HeaderInfo(ip, httpMethod, uri, username);
	}

	public String getClientIp(HttpServletRequest request) {
		return extractClientIp(request);
	}

	/// 쿠키 생성하기
	private void createCookie(HttpServletResponse response, String cookieName, String cookieValue, long maxAge) {

		ResponseCookie cookie = ResponseCookie.from(cookieName, cookieValue)
			.maxAge(maxAge)
			.path(cookiePathOption)
			.httpOnly(true)
			.secure(secureOption)  // Dev/Prod 환경에 따라 설정됨
			.sameSite(sameSiteOption)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

	}

	// =================
	//  내부 공통 함수
	// =================

	/// Authorization Bearer 헤더에서 액세스 토큰 가져오기
	private Optional<String> extractBearerAuthorizationToken(HttpServletRequest request) {
		return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
			.map(String::trim)
			.filter(headerValue -> headerValue.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length()))
			.map(headerValue -> headerValue.substring(BEARER_PREFIX.length()).trim())
			.filter(token -> !token.isEmpty());
	}

	/// 사용자 정의 헤더에서 토큰 가져오기
	private Optional<String> extractHeaderToken(HttpServletRequest request, String headerName) {
		return Optional.ofNullable(request.getHeader(headerName))
			.map(String::trim)
			.filter(headerValue -> !headerValue.isEmpty())
			.map(this::stripBearerPrefixIfPresent)
			.filter(token -> !token.isEmpty());
	}

	/// 쿠키에서 토큰 가져오기
	private Optional<String> extractCookieToken(HttpServletRequest httpServletRequest, String type) {

		/// 쿠키 가져오기
		Cookie[] cookies = httpServletRequest.getCookies();

		/// 쿠키가 존재한다면,
		if (cookies != null) {
			for (Cookie cookie : cookies) {

				/// 해당 타입의 쿠키만 추출
				if (cookie.getName().equals(type)) {
					return Optional.of(cookie.getValue());
				}
			}
		}
		return Optional.empty();
	}

	private String stripBearerPrefixIfPresent(String headerValue) {
		if (!headerValue.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
			return headerValue;
		}
		return headerValue.substring(BEARER_PREFIX.length()).trim();
	}

	/// 공통 쿠키 삭제 메서드
	private void deleteCookie(HttpServletResponse response, String cookieName) {
		ResponseCookie cookie = ResponseCookie.from(cookieName, "")
			.maxAge(0)
			.path(cookiePathOption)
			.secure(secureOption)
			.httpOnly(true)
			.sameSite(sameSiteOption)
			.build();

		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	/// 요청자의 실제 IP를 조회하기 위한 함수
	private String extractClientIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		/// X-Forwarded-For이 있다면
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			// 여러 개라면 첫 번째 값이 클라이언트 IP
			return ip.split(",")[0].trim();
		}

		/// X-Forwarded-For이 없다면
		ip = request.getHeader("X-Real-IP");
		if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}

	/// 헤더의 값을 전달하기 위해서 레코드 클래스 생성
	public record HeaderInfo(String ip, String httpMethod, String uri, String userName) {

	}
}
