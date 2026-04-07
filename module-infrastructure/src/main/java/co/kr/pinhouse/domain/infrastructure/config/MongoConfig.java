package co.kr.pinhouse.domain.infrastructure.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * MongoDB 연결 및 템플릿 설정을 위한 구성 클래스입니다.
 * AbstractMongoClientConfiguration을 상속받아 전반적인 몽고DB 인프라를 수동 설정합니다.
 */
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	@Value("${spring.data.mongodb.uri}")
	private String connectionString;

	@Value("${spring.data.mongodb.database}")
	private String databaseName;

	// MongoClient 설정을 추가로 커스터마이징할 수 있는 커스터마이저들의 목록을 제공받습니다.
	private final ObjectProvider<MongoClientSettingsBuilderCustomizer> mongoClientSettingsBuilderCustomizers;

	public MongoConfig(ObjectProvider<MongoClientSettingsBuilderCustomizer> mongoClientSettingsBuilderCustomizers) {
		this.mongoClientSettingsBuilderCustomizers = mongoClientSettingsBuilderCustomizers;
	}

	/**
	 * MongoDB와의 상호작용을 담당하는 핵심 템플릿 빈을 생성합니다.
	 * @param mongoClient 생성된 MongoClient 빈
	 * @return 지정된 데이터베이스를 사용하는 MongoTemplate 객체
	 */
	@Bean
	public MongoTemplate mongoTemplate(MongoClient mongoClient) {
		return new MongoTemplate(mongoClient, databaseName);
	}

	/**
	 * MongoDB 서버에 연결할 클라이언트를 구성합니다.
	 * @return 설정이 완료된 MongoClient 인스턴스
	 */
	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(this.connectionString);

		MongoClientSettings.Builder mongoClientSettingsBuilder = MongoClientSettings.builder()
			.applyConnectionString(connectionString);

		// 등록된 모든 MongoClientSettingsBuilderCustomizer를 순차적으로 적용합니다.
		// 이를 통해 다른 설정 클래스(TracingConfig)에서 정의한 추가 설정들이 반영됩니다.
		mongoClientSettingsBuilderCustomizers.orderedStream()
			.forEach(customizer -> customizer.customize(mongoClientSettingsBuilder));

		MongoClientSettings mongoClientSettings = mongoClientSettingsBuilder.build();

		// 실제 MongoClient 생성
		return MongoClients.create(mongoClientSettings);
	}

	/**
	 * 사용할 데이터베이스 이름을 반환하도록 구현합니다.
	 */
	@Override
	protected String getDatabaseName() {
		return databaseName;
	}
}
