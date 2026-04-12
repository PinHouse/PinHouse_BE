package co.kr.pinhouse.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedirectUrlResolver {

	@Value("${cors.front.local}")
	private String frontLocal;

	@Value("${cors.front.dev}")
	private String frontDev;

	@Value("${cors.front.prod}")
	private String frontProd;

	@Value("${cors.front.redirect}")
	private String defaultRedirectUrl;

	@Value("${spring.profiles.active:local}")
	private String activeProfile;

	private Set<String> allowedOrigins;

	@PostConstruct
	private void initAllowedOrigins() {
		allowedOrigins = new HashSet<>();
		addIfNotEmpty(allowedOrigins, frontLocal);
		addIfNotEmpty(allowedOrigins, frontDev);
		addIfNotEmpty(allowedOrigins, frontProd);

		log.info("RedirectUrlResolver 초기화 완료 - 활성 프로파일: {}, 허용된 Origin 개수: {}",
			activeProfile, allowedOrigins.size());
	}

	/**
	 * 요청에서 리다이렉트 URL을 결정합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @return 결정된 리다이렉트 URL
	 */
	public String resolveRedirectUrl(HttpServletRequest request) {
		return resolveRedirectUrlWithPath(request, "");
	}

	/**
	 * 요청에서 리다이렉트 URL을 결정하고 경로를 추가합니다.
	 *
	 * @param request HTTP 요청 객체
	 * @param path 추가할 경로 (예: "/signup?state=abc")
	 * @return 결정된 리다이렉트 URL + 경로
	 */
	public String resolveRedirectUrlWithPath(HttpServletRequest request, String path) {
		// prod 프로파일: 항상 고정 URL
		if ("prod".equals(activeProfile)) {
			String redirectUrl = frontProd + (path != null ? path : "");
			log.info("리다이렉트 URL 결정 (prod 고정) - URL: {}", redirectUrl);
			return redirectUrl;
		}

		// dev/local 프로파일: 동적 결정
		String origin = extractOriginFromRequest(request);

		if (isAllowedOrigin(origin)) {
			String redirectUrl = origin + (path != null ? path : "");
			log.info("리다이렉트 URL 결정 (동적) - Origin: {}, URL: {}", origin, redirectUrl);
			return redirectUrl;
		}

		// 검증 실패 시 기본 URL 사용
		String redirectUrl = defaultRedirectUrl + (path != null ? path : "");
		log.warn("요청 Origin이 허용되지 않음: {}. 기본 URL 사용: {}", origin, redirectUrl);
		return redirectUrl;
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
				log.warn("허용되지 않은 프로토콜: {}", scheme);
				return null;
			}

			String origin = scheme + "://" + host;
			if (port != -1 && port != 80 && port != 443) {
				origin += ":" + port;
			}

			return normalizeOrigin(origin);
		} catch (URISyntaxException e) {
			log.warn("Referer 파싱 실패: {}", referer, e);
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

	/**
	 * Origin을 정규화합니다 (trailing slash 제거, 소문자 변환).
	 *
	 * @param origin 정규화할 Origin
	 * @return 정규화된 Origin
	 */
	private String normalizeOrigin(String origin) {
		return origin.replaceAll("/$", "").toLowerCase();
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
}
