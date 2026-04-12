package co.kr.pinhouse.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.api.gax.retrying.RetrySettings;
import com.google.cloud.NoCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * GCS Emulator 설정 클래스
 * 로컬 개발 환경에서 fake-gcs-server를 사용
 */
@Configuration
@Profile("local")  // local 프로파일일 때만 활성화
public class GcsEmulatorConfig {

	@Value("${gcs.emulator.host:http://localhost:4443}")
	private String emulatorHost;

	@Value("${gcs.project-id}")
	private String projectId;

	@Bean
	public Storage gcsStorage() {
		return StorageOptions.newBuilder()
			.setHost(emulatorHost)
			.setProjectId(projectId)
			.setCredentials(NoCredentials.getInstance())
			.setRetrySettings(RetrySettings.newBuilder().setMaxAttempts(1).build())
			.build()
			.getService();
	}
}
