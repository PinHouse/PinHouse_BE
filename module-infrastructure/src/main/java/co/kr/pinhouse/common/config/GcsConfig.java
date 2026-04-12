package co.kr.pinhouse.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

/**
 * Google Cloud Storage 클라이언트 설정 클래스
 * Production 환경에서 Application Default Credentials를 사용
 */
@Configuration
@Profile("!local")  // local 프로파일이 아닐 때 활성화
public class GcsConfig {

	@Value("${gcs.project-id}")
	private String projectId;

	/**
	 * GCS Storage Client Bean 생성
	 * ADC(Application Default Credentials)를 통해 자동 인증
	 */
	@Bean
	public Storage gcsStorage() {
		return StorageOptions.newBuilder()
			.setProjectId(projectId)
			// .setCredentials() 생략 시 ADC 자동 사용 (VM 서비스 어카운트)
			.build()
			.getService();
	}
}
