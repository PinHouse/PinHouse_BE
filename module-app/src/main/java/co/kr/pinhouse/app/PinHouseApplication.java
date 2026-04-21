package co.kr.pinhouse.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication(scanBasePackages = "co.kr.pinhouse")
@EntityScan(basePackages = "co.kr.pinhouse.domain")
@EnableJpaRepositories(basePackages = {
	"co.kr.pinhouse.domain.user.domain.repository",
	"co.kr.pinhouse.domain.diagnostic.diagnosis.domain.repository",
	"co.kr.pinhouse.domain.diagnostic.school.domain.repository",
	"co.kr.pinhouse.domain.like.domain",
	"co.kr.pinhouse.domain.admin.audit.domain.repository",
	"co.kr.pinhouse.domain.admin.notice.domain.repository",
	"co.kr.pinhouse.domain.cs.domain.repository",
	"co.kr.pinhouse.domain.ad.domain.repository"
})
@EnableMongoRepositories(basePackages = {
	"co.kr.pinhouse.domain.housing.complex.domain.repository",
	"co.kr.pinhouse.domain.housing.notice.domain.repository",
	"co.kr.pinhouse.domain.housing.facility.domain.repository",
	"co.kr.pinhouse.domain.pinpoint.domain.repository",
	"co.kr.pinhouse.domain.search.domain.repository"
})
@EnableRedisRepositories(basePackages = {
	"co.kr.pinhouse.security.jwt.domain.repository"
})
@EnableJpaAuditing
public class PinHouseApplication {

	public static void main(String[] args) {
		SpringApplication.run(PinHouseApplication.class, args);
	}
}
