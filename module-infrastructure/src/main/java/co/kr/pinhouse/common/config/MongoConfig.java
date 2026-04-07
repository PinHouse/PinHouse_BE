package co.kr.pinhouse.common.config;

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

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

	private final ObjectProvider<MongoClientSettingsBuilderCustomizer> mongoClientSettingsBuilderCustomizers;
	@Value("${spring.data.mongodb.uri}")
	private String connectionString;
	@Value("${spring.data.mongodb.database}")
	private String databaseName;

	public MongoConfig(ObjectProvider<MongoClientSettingsBuilderCustomizer> mongoClientSettingsBuilderCustomizers) {
		this.mongoClientSettingsBuilderCustomizers = mongoClientSettingsBuilderCustomizers;
	}

	@Bean
	public MongoTemplate mongoTemplate(MongoClient mongoClient) {
		return new MongoTemplate(mongoClient, databaseName);
	}

	@Override
	public MongoClient mongoClient() {
		ConnectionString connectionString = new ConnectionString(this.connectionString);
		MongoClientSettings.Builder mongoClientSettingsBuilder = MongoClientSettings.builder()
			.applyConnectionString(connectionString);

		mongoClientSettingsBuilderCustomizers.orderedStream()
			.forEach(customizer -> customizer.customize(mongoClientSettingsBuilder));

		MongoClientSettings mongoClientSettings = mongoClientSettingsBuilder.build();

		return MongoClients.create(mongoClientSettings);
	}

	@Override
	protected String getDatabaseName() {
		return databaseName;
	}
}
