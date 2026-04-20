package co.kr.pinhouse.common.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class KeyUtil {

	/// JWT
	public static final String ACCESS_TOKEN = "access_token";
	public static final String REFRESH_TOKEN = "refresh_token";

	/// 합쳐서 사용하는 키 목록
	public static final String ID_CLAIM = "user_id";
	public static final String ROLE_CLAIM = "role";
	/// 예외
	public static final String HTTP_ERROR_401 = "[HTTP_인증 실패]";
	public static final String HTTP_ERROR_403 = "[HTTP_인가 실패]";
	/// 공통 키
	private static final String SEPARATOR = ":";
	private static final String DELIMITER = "-";
	/// OAUTH
	private static final String OAUTH2_TEMP_USER = "OAUTH2_TEMP_USER:";
	/// EXCHANGE CODE
	private static final String EXCHANGE_CODE = "EXCHANGE_CODE:";
	private static final String EXCHANGE_CODE_RATE_LIMIT = "EXCHANGE_CODE_RATE_LIMIT:";

	// =====================
	//  합쳐서 사용하는 키 목록
	// =====================

	public static String getRefreshTokenKey(UUID userId) {
		return REFRESH_TOKEN + SEPARATOR + userId;
	}

	public static String getExchangeCodeKey(UUID userId) {
		return EXCHANGE_CODE + userId;
	}

	public static String getExchangeCodeKey(String tempUserKey) {
		return EXCHANGE_CODE + tempUserKey;
	}

	public static String getExchangeCodeRateLimitKey(String clientIdentifier) {
		return EXCHANGE_CODE_RATE_LIMIT + clientIdentifier;
	}

	// =====================
	//  키 생성 함수
	// =====================

	/// 키 생성 함수
	public String generateOAuth2TempUserKey() {
		return OAUTH2_TEMP_USER + UUID.randomUUID();
	}

	/// Exchange code 생성 (암호학적으로 안전한 48자 랜덤 문자열)
	public String generateExchangeCode() {
		return UUID.randomUUID().toString().replace("-", "")
			+ UUID.randomUUID().toString().replace("-", "").substring(0, 16);
	}

}
