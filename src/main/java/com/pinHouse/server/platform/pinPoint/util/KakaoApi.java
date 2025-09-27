package com.pinHouse.server.platform.pinPoint.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.platform.Location;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 카카오API 좌표변환 활용 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class KakaoApi implements LocationUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    private static final String KAKAO_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    /**
     * 좌표 변환 로직
     * @param address   변환할 주소
     */
    @Override
    public Location getLocation(String address) {
        // 헤더 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

        // 파라미터 포함
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
                double latitude = first.path("y").asDouble();  // 위도
                double longitude = first.path("x").asDouble(); // 경도

                return Location.of(longitude, latitude);
            }
        } catch (Exception e) {
            throw new RuntimeException("Kakao API 호출 실패: " + e.getMessage());
        }

        return null;
    }
}
