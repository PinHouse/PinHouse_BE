package com.pinHouse.server.platform.housing.distance.application.service;

import com.pinHouse.server.platform.housing.distance.application.dto.response.DistanceResponse;
import com.pinHouse.server.platform.housing.distance.application.usecase.DistanceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OdsayService implements DistanceUseCase {

    @Value("${odsay.apiKey}")
    private String apiKey;

    private final WebClient webClient;

    @Override
    public List<DistanceResponse> findPath(double startY, double startX, double endY, double endX) throws UnsupportedEncodingException {
        String encodedApiKey = URLEncoder.encode(apiKey, "UTF-8");

        String uri = UriComponentsBuilder.fromHttpUrl("https://api.odsay.com/v1/api/searchPubTransPathT")
                .queryParam("SX", startX)
                .queryParam("SY", startY)
                .queryParam("EX", endX)
                .queryParam("EY", endY)
                .queryParam("apiKey", encodedApiKey)
                .build(false) // 인코딩 방지
                .toUriString();

        try {
            var response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorMap(e -> new RuntimeException("ODsay API 호출 실패", e))
                    .block();// 동기 방식

            return DistanceResponse.of(response);
        } catch (Exception e) {
            throw new RuntimeException("ODsay API 호출 실패", e);
        }
    }

}

