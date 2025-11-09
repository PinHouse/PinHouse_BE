package com.pinHouse.server.platform.housing.complex.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinHouse.server.core.exception.code.ComplexErrorCode;
import com.pinHouse.server.core.response.response.CustomException;
import com.pinHouse.server.platform.housing.complex.application.dto.result.PathResult;
import com.pinHouse.server.platform.housing.complex.application.util.DistanceUtil;
import com.pinHouse.server.platform.housing.complex.application.util.InterCityResultParser;
import com.pinHouse.server.platform.housing.complex.application.util.IntraCityResultParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OdsayUtil implements DistanceUtil {

    @Value("${odsay.apiKey}")
    private String apiKey;

    private final WebClient webClient;
    private static final ObjectMapper OM = new ObjectMapper();

    // =================
    //  퍼블릭 로직
    // =================

    @Override
    public PathResult findPathResult(double startY, double startX, double endY, double endX)
            throws UnsupportedEncodingException {

        String encodedApiKey = URLEncoder.encode(apiKey, "UTF-8");

        String uri = UriComponentsBuilder.fromHttpUrl("https://api.odsay.com/v1/api/searchPubTransPathT")
                .queryParam("SX", startX)
                .queryParam("SY", startY)
                .queryParam("EX", endX)
                .queryParam("EY", endY)
                .queryParam("apiKey", encodedApiKey)
                .build(false) // 추가 인코딩 방지
                .toUriString();

        /// 값 호출
        try {
            String response = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(String.class)
                    .onErrorMap(e -> new CustomException(ComplexErrorCode.ODSAY_SERVER_ERROR))
                    .block(); // 동기

            /// 자동 판별
            JsonNode root = OM.readTree(response);
            int searchType = detectSearchType(root);

            /// 분기 처리
            if (searchType == 0) {
                /// 도시내
                return IntraCityResultParser.parse(root);
            } else {
                /// 도시간(직통/환승 등 포함)
                return InterCityResultParser.parse(root);
            }

        } catch (Exception e) {
            throw new CustomException(ComplexErrorCode.ODSAY_PARSING_ERROR);
        }
        
    }

    // =================
    //  내부 로직
    // =================

    /// 응답의 도시내/ 도시간 기반 판별.
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

}

