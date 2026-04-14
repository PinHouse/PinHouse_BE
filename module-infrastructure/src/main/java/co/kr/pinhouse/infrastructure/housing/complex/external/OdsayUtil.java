package co.kr.pinhouse.infrastructure.housing.complex.external;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.pinhouse.common.exception.code.ComplexErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.housing.complex.application.util.DistanceUtil;
import co.kr.pinhouse.domain.housing.complex.application.util.InterCityResultParser;
import co.kr.pinhouse.domain.housing.complex.application.util.IntraCityResultParser;
import co.kr.pinhouse.domain.housing.complex.domain.transit.PathResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OdsayUtil implements DistanceUtil {

	private static final ObjectMapper OM = new ObjectMapper();
	private static final String ODSAY_PATH_URL = "https://api.odsay.com/v1/api/searchPubTransPathT";
	private final WebClient webClient;

	@Value("${odsay.apiKey}")
	private String apiKey;

	// =================
	//  퍼블릭 로직
	// =================

	@Override
	public PathResult findPathResult(double startY, double startX, double endY, double endX) {

		String normalizedApiKey = normalizeApiKey(apiKey);
		if (normalizedApiKey == null || normalizedApiKey.isBlank()) {
			log.error("ODsay API Key가 비어 있습니다");
			throw new CustomException(ComplexErrorCode.ODSAY_INVALID_API_KEY);
		}

		/// ODsay 안내에 따라 API Key는 1회 인코딩 후 직접 URI에 반영
		String encodedApiKey = encodeApiKey(normalizedApiKey);
		URI requestUri = URI.create(buildRequestUri(startY, startX, endY, endX, encodedApiKey));

		/// 값 호출
		try {
			String response = webClient.get()
				.uri(requestUri)
				.retrieve()
				.bodyToMono(String.class)
				.onErrorMap(e -> new CustomException(ComplexErrorCode.ODSAY_SERVER_ERROR))
				.block(); // 동기

			if (response == null || response.isBlank()) {
				log.error("ODsay 응답 본문이 비어 있습니다");
				throw new CustomException(ComplexErrorCode.ODSAY_PARSING_ERROR);
			}

			/// 자동 판별
			JsonNode root = OM.readTree(response);
			handleOdsayError(root);
			ensurePathExists(root);

			int searchType = detectSearchType(root);

			/// 분기 처리
			if (searchType == 0) {
				/// 도시내
				return IntraCityResultParser.parse(root);
			} else {
				/// 도시간(직통/환승 등 포함)
				return InterCityResultParser.parse(root);
			}

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("ODsay 응답 파싱에 실패했습니다", e);
			throw new CustomException(ComplexErrorCode.ODSAY_PARSING_ERROR);
		}

	}

	// =================
	//  내부 로직
	// =================

	/// 응답의 도시내/도시간 기반 판별.
	/// searchType=1/2인 도시간 경로도 세부 subPath에는 지하철/시내버스가 섞여 들어올 수 있으므로,
	/// 개별 교통수단이 아니라 최상위 result 기준으로 파서를 결정한다.
	private int detectSearchType(JsonNode root) {
		JsonNode result = root.path("result");

		/// searchType보고 반영
		if (result.has("searchType")) {
			return result.path("searchType").asInt(0);
		}

		/// searchType 없을때, trainCount/airCount/mixedCount 있으면 도시간
		boolean hasIntercityHints =
			result.has("trainCount") || result.has("airCount") || result.has("mixedCount");
		return hasIntercityHints ? 1 : 0;
	}

	/// ODsay 에러 응답을 비즈니스 예외로 변환
	private void handleOdsayError(JsonNode root) {
		JsonNode errorNode = root.path("error");
		if (errorNode.isMissingNode() || errorNode.isNull() || errorNode.size() == 0) {
			return;
		}

		String errorCode = readText(errorNode, "code");
		String errorMessage = firstNonBlank(
			readText(errorNode, "msg"),
			readText(errorNode, "message"),
			errorNode.toString()
		);

		log.error("ODsay 에러 응답 수신 - code={}, message={}", errorCode, errorMessage);

		if (isInvalidApiKey(errorCode, errorMessage)) {
			throw new CustomException(ComplexErrorCode.ODSAY_INVALID_API_KEY);
		}

		throw new CustomException(ComplexErrorCode.ODSAY_ERROR_RESPONSE);
	}

	/// 응답에 실제 경로 목록이 존재하는지 검증
	private void ensurePathExists(JsonNode root) {
		JsonNode paths = root.path("result").path("path");
		if (!paths.isArray() || paths.isEmpty()) {
			log.warn("ODsay가 요청 좌표에 대한 대중교통 경로를 반환하지 않았습니다");
			throw new CustomException(ComplexErrorCode.NOT_FOUND_TRANSIT_ROUTE);
		}
	}

	private boolean isInvalidApiKey(String errorCode, String errorMessage) {
		String normalized = firstNonBlank(errorCode, "") + " " + firstNonBlank(errorMessage, "");
		String lower = normalized.toLowerCase(Locale.ROOT);
		return lower.contains("apikey")
			|| lower.contains("api key")
			|| lower.contains("access key")
			|| (lower.contains("key") && lower.contains("invalid"))
			|| (lower.contains("key") && lower.contains("failed"));
	}

	private String readText(JsonNode node, String fieldName) {
		JsonNode fieldNode = node.path(fieldName);
		if (fieldNode.isMissingNode() || fieldNode.isNull()) {
			return null;
		}

		String value = fieldNode.asText(null);
		return value == null || value.isBlank() ? null : value;
	}

	private String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}

		return null;
	}

	/// API Key를 한 번만 인코딩하고 공백은 + 로 통일
	private String encodeApiKey(String normalizedApiKey) {
		return URLEncoder.encode(normalizedApiKey, StandardCharsets.UTF_8)
			.replace(" ", "+");
	}

	/// WebClient가 재인코딩하지 않도록 완성된 URI를 직접 생성
	private String buildRequestUri(double startY, double startX, double endY, double endX, String encodedApiKey) {
		return UriComponentsBuilder.fromUriString(ODSAY_PATH_URL)
			.queryParam("SX", startX)
			.queryParam("SY", startY)
			.queryParam("EX", endX)
			.queryParam("EY", endY)
			.build(true)
			.toUriString() + "&apiKey=" + encodedApiKey;
	}

	/// API Key 앞뒤 공백과 감싼 따옴표 제거
	private String normalizeApiKey(String rawApiKey) {
		if (rawApiKey == null) {
			return null;
		}

		String normalized = rawApiKey.trim();
		if (normalized.length() >= 2) {
			boolean wrappedWithDoubleQuotes = normalized.startsWith("\"") && normalized.endsWith("\"");
			boolean wrappedWithSingleQuotes = normalized.startsWith("'") && normalized.endsWith("'");
			if (wrappedWithDoubleQuotes || wrappedWithSingleQuotes) {
				normalized = normalized.substring(1, normalized.length() - 1).trim();
			}
		}

		return normalized;
	}

}
