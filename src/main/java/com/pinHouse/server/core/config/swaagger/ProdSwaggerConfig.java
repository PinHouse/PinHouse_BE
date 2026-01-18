package com.pinHouse.server.core.config.swaagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Map;
import java.util.TreeMap;

/**
 * Swagger / OpenAPI 설정 클래스
 * - HTTPS 설정으로 인해 @OpenAPIDefinition 추가
 * - 스웨거 내부 JWT 설정 추가
 */

@Configuration
@Profile("prod")
@OpenAPIDefinition(servers = {@Server(url = "https://api.pinhouse.co.kr", description = "PinHouse 운영 서버")})
public class ProdSwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo());
    }


    private Info apiInfo() {
        return new Info()
                .title("PinHouse Swagger")
                .description("핀하우스 스웨거입니다.")
                .version("1.0.0");
    }

    /// 스키마 이름 기준 오름차순
    @Bean
    public OpenApiCustomizer sortSchemasAlphabetically() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            openApi.getComponents().setSchemas(new TreeMap<>(schemas));
        };
    }
}
