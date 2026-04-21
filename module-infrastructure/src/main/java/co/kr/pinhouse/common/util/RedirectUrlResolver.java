package co.kr.pinhouse.common.util;

import static co.kr.pinhouse.common.util.LogSanitizer.sanitize;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedirectUrlResolver {

	public static final String REDIRECT_ORIGIN_SESSION_ATTRIBUTE = "PINHOUSE_REDIRECT_ORIGIN";
	public static final String REDIRECT_PATH_SESSION_ATTRIBUTE = "PINHOUSE_REDIRECT_PATH";

	@Value("${cors.front.local}")
	private String frontLocal;

	@Value("${cors.front.dev}")
	private String frontDev;

	@Value("${cors.front.prod}")
	private String frontProd;

	@Value("${cors.front.admin-local:}")
	private String adminFrontLocal;

	@Value("${cors.front.admin-dev:}")
	private String adminFrontDev;

	@Value("${cors.front.admin-prod:}")
	private String adminFrontProd;

	@Value("${cors.front.redirect}")
	private String defaultRedirectUrl;

	@Value("${cors.front.redirect-path:/api/auth/callback}")
	private String defaultRedirectPath;

	@Value("${cors.front.admin-redirect-path:/admin/auth/callback}")
	private String defaultAdminRedirectPath;

	@Value("${spring.profiles.active:local}")
	private String activeProfile;

	private Set<String> allowedOrigins;
	private Set<String> adminOrigins;

	@PostConstruct
	private void initAllowedOrigins() {
		allowedOrigins = new HashSet<>();
		adminOrigins = new HashSet<>();
		addIfNotEmpty(allowedOrigins, frontLocal);
		addIfNotEmpty(allowedOrigins, frontDev);
		addIfNotEmpty(allowedOrigins, frontProd);
		addAdminOriginIfNotEmpty(adminFrontLocal);
		addAdminOriginIfNotEmpty(adminFrontDev);
		addAdminOriginIfNotEmpty(adminFrontProd);

		log.info("RedirectUrlResolver 초기화 완료 - 활성 프로파일: {}, 허용된 Origin 개수: {}",
			sanitize(activeProfile), sanitize(allowedOrigins.size()));
	}

	/**
	 * 요청에서 리다이렉트 URL을 결정합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @return 결정된 리다이렉트 URL
	 */
	public String resolveRedirectUrl(HttpServletRequest request) {
		return resolveRedirectUrlWithPath(request, null);
	}

	/**
	 * 요청에서 리다이렉트 URL을 결정하고 경로를 추가합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @param path 추가할 경로 (예: "/signup?state=abc")
	 * @return 결정된 리다이렉트 URL + 경로
	 */
	public String resolveRedirectUrlWithPath(HttpServletRequest request, String path) {
		RedirectTarget target = consumeSavedRedirectTarget(request);
		String origin = target.origin();
		String redirectPath = normalizeRedirectPath(path);

		if (redirectPath == null) {
			redirectPath = target.redirectPath();
		}
		if (redirectPath == null) {
			redirectPath = getDefaultRedirectPath(origin);
		}

		if (isAllowedOrigin(origin)) {
			String redirectUrl = buildRedirectUrl(origin, redirectPath);
			log.info("리다이렉트 URL 결정 - Origin: {}, path: {}, URL: {}",
				sanitize(origin), sanitize(redirectPath), sanitize(redirectUrl));
			return redirectUrl;
		}

		// 검증 실패 시 기본 URL 사용
		String defaultPath = getDefaultRedirectPath(origin);
		String redirectUrl = buildRedirectUrl(defaultRedirectUrl, redirectPath != null ? redirectPath : defaultPath);
		log.warn("요청 Origin이 허용되지 않음: {}. 기본 URL 사용: {}", sanitize(origin), sanitize(redirectUrl));
		return redirectUrl;
	}

	/**
	 * OAuth2 인증 시작 요청의 Origin을 세션에 저장합니다.
	 *
	 * @param request HTTP 요청 객체
	 */
	public void saveRedirectContext(HttpServletRequest request) {
		String origin = extractOriginFromRequest(request);

		if (!isAllowedOrigin(origin)) {
			log.debug("저장 가능한 OAuth2 Origin이 없습니다. Origin: {}", sanitize(origin));
			return;
		}

		String redirectPath = extractRedirectPath(request, origin);

		// OAuth 공급자 페이지를 거쳐 돌아와도 원래 프론트로 복귀할 수 있도록 세션에 보관
		request.getSession(true).setAttribute(REDIRECT_ORIGIN_SESSION_ATTRIBUTE, origin);
		request.getSession(true).setAttribute(REDIRECT_PATH_SESSION_ATTRIBUTE, redirectPath);
		log.info("OAuth2 리다이렉트 정보 저장 - Origin: {}, path: {}", sanitize(origin), sanitize(redirectPath));
	}

	/**
	 * 요청에서 Origin을 추출합니다.
	 * Origin 헤더를 우선 사용하고, 없으면 Referer 헤더에서 추출합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @return 추출된 Origin (없으면 null)
	 */
	private String extractOriginFromRequest(HttpServletRequest request) {
		// 1. Origin 헤더 우선
		String origin = request.getHeader("Origin");
		if (origin != null && !origin.isEmpty()) {
			return normalizeOrigin(origin);
		}

		// 2. Referer 헤더에서 추출
		String referer = request.getHeader("Referer");
		if (referer != null && !referer.isEmpty()) {
			return extractOriginFromReferer(referer);
		}

		return null;
	}

	/**
	 * 세션에 저장된 Origin을 조회 후 제거합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @return 저장된 Origin (없으면 null)
	 */
	private RedirectTarget consumeSavedRedirectTarget(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Object savedOrigin = session.getAttribute(REDIRECT_ORIGIN_SESSION_ATTRIBUTE);
			Object savedPath = session.getAttribute(REDIRECT_PATH_SESSION_ATTRIBUTE);
			session.removeAttribute(REDIRECT_ORIGIN_SESSION_ATTRIBUTE);
			session.removeAttribute(REDIRECT_PATH_SESSION_ATTRIBUTE);

			if (savedOrigin instanceof String origin && !origin.isBlank()) {
				return new RedirectTarget(normalizeOrigin(origin), normalizeRedirectPath((String)savedPath));
			}
		}

		String origin = extractOriginFromRequest(request);
		return new RedirectTarget(origin, null);
	}

	/**
	 * Referer 헤더에서 Origin을 추출합니다.
	 *
	 * @param referer Referer 헤더 값
	 * @return 추출된 Origin (실패 시 null)
	 */
	private String extractOriginFromReferer(String referer) {
		try {
			URI uri = new URI(referer);
			String scheme = uri.getScheme();
			String host = uri.getHost();
			int port = uri.getPort();

			if (scheme == null || host == null) {
				return null;
			}

			// 프로토콜 검증
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				log.warn("허용되지 않은 프로토콜: {}", sanitize(scheme));
				return null;
			}

			String origin = scheme + "://" + host;
			if (port != -1 && port != 80 && port != 443) {
				origin += ":" + port;
			}

			return normalizeOrigin(origin);
		} catch (URISyntaxException e) {
			log.warn("Referer 파싱 실패: {}", sanitize(referer), e);
			return null;
		}
	}

	/**
	 * Origin이 허용된 목록에 포함되어 있는지 검증합니다.
	 *
	 * @param origin 검증할 Origin
	 * @return 허용 여부
	 */
	private boolean isAllowedOrigin(String origin) {
		if (origin == null || origin.isEmpty()) {
			return false;
		}
		return allowedOrigins.contains(origin);
	}

	private boolean isAdminOrigin(String origin) {
		if (origin == null || origin.isEmpty()) {
			return false;
		}
		return adminOrigins.contains(origin);
	}

	/**
	 * Origin을 정규화합니다 (trailing slash 제거, 소문자 변환).
	 *
	 * @param origin 정규화할 Origin
	 * @return 정규화된 Origin
	 */
	private String normalizeOrigin(String origin) {
		return origin.replaceAll("/$", "").toLowerCase();
	}

	private String extractRedirectPath(HttpServletRequest request, String origin) {
		String requestedPath = request.getParameter("redirectPath");
		String redirectPath = normalizeRedirectPath(requestedPath);

		if (redirectPath != null) {
			return redirectPath;
		}

		if (requestedPath != null && !requestedPath.isBlank()) {
			log.warn("허용되지 않은 redirectPath: {}", sanitize(requestedPath));
		}

		return getDefaultRedirectPath(origin);
	}

	private String normalizeRedirectPath(String path) {
		if (path == null || path.isBlank()) {
			return null;
		}

		String normalizedPath = path.trim();
		if (!normalizedPath.startsWith("/") || normalizedPath.startsWith("//")) {
			return null;
		}
		if (normalizedPath.indexOf('\r') >= 0 || normalizedPath.indexOf('\n') >= 0) {
			return null;
		}

		try {
			URI uri = new URI(normalizedPath);
			if (uri.isAbsolute() || uri.getHost() != null || uri.getAuthority() != null) {
				return null;
			}
			return normalizedPath;
		} catch (URISyntaxException exception) {
			return null;
		}
	}

	private String getDefaultRedirectPath(String origin) {
		String redirectPath = isAdminOrigin(origin) ? defaultAdminRedirectPath : defaultRedirectPath;
		String normalizedPath = normalizeRedirectPath(redirectPath);
		return normalizedPath != null ? normalizedPath : "/api/auth/callback";
	}

	private String buildRedirectUrl(String origin, String path) {
		return normalizeOrigin(origin) + path;
	}

	/**
	 * 값이 비어있지 않으면 Set에 추가합니다.
	 *
	 * @param set 추가할 Set
	 * @param value 추가할 값
	 */
	private void addIfNotEmpty(Set<String> set, String value) {
		if (value != null && !value.isEmpty()) {
			set.add(normalizeOrigin(value));
		}
	}

	private void addAdminOriginIfNotEmpty(String origin) {
		if (origin != null && !origin.isBlank()) {
			String normalizedOrigin = normalizeOrigin(origin);
			allowedOrigins.add(normalizedOrigin);
			adminOrigins.add(normalizedOrigin);
		}
	}

	private record RedirectTarget(String origin, String redirectPath) {
	}
}
