package co.kr.pinhouse.infrastructure.pinpoint.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.kr.pinhouse.common.exception.code.PinPointErrorCode;
import co.kr.pinhouse.common.response.CustomException;
import co.kr.pinhouse.domain.Location;
import co.kr.pinhouse.domain.pinpoint.util.LocationUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoApi implements LocationUtil {

	private static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/search/address.json";
	private final RestTemplate restTemplate = new RestTemplate();
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${kakao.rest-api-key}")
	private String kakaoRestApiKey;

	@Override
	public Location getLocation(String address) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

		String url = KAKAO_URL + "?query=" + address;
		HttpEntity<String> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(
			url, HttpMethod.GET, entity, String.class
		);

		try {
			JsonNode root = objectMapper.readTree(response.getBody());
			JsonNode documents = root.path("documents");
			if (documents.isArray() && documents.size() > 0) {
				JsonNode first = documents.get(0);
				double latitude = first.path("y").asDouble();
				double longitude = first.path("x").asDouble();

				return Location.of(longitude, latitude);
			}
		} catch (Exception e) {
			throw new CustomException(PinPointErrorCode.KAKAO_SERVER_ERROR);
		}

		return null;
	}
}
