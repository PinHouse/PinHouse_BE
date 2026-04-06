package com.pinHouse.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(scanBasePackages = "com.pinHouse")
@EntityScan(basePackages = "com.pinHouse.domain")
@EnableJpaRepositories(basePackages = {
	"com.pinHouse.domain.user.domain.repository",
	"com.pinHouse.domain.diagnostic.diagnosis.domain.repository",
	"com.pinHouse.domain.diagnostic.school.domain.repository",
	"com.pinHouse.domain.like.domain"
})
@EnableMongoRepositories(basePackages = {
	"com.pinHouse.domain.housing.complex.domain.repository",
	"com.pinHouse.domain.housing.notice.domain.repository",
	"com.pinHouse.domain.housing.facility.domain.repository",
	"com.pinHouse.domain.pinPoint.domain.repository",
	"com.pinHouse.domain.search.domain.repository"
})
@EnableRedisRepositories(basePackages = {
	"com.pinHouse.security.jwt.domain.repository"
})
@EnableJpaAuditing
public class PinHouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinHouseApplication.class, args);
	}
}
