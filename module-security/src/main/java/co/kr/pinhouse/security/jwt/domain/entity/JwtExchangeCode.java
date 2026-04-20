package co.kr.pinhouse.security.jwt.domain.entity;

import static co.kr.pinhouse.common.util.KeyUtil.getExchangeCodeKey;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash
public class JwtExchangeCode {

	@Id
	private String id;

	@Indexed
	private UUID userId;

	@Indexed
	private String tempUserKey;

	@Indexed
	private String exchangeCode;

	private JwtExchangeCodeType exchangeCodeType;

	@TimeToLive(unit = TimeUnit.SECONDS)
	private long expireTime;

	/// 빌더 생성자
	@Builder
	public JwtExchangeCode(String id, UUID userId, String tempUserKey, String exchangeCode,
		JwtExchangeCodeType exchangeCodeType, long expireTime) {
		this.id = id;
		this.userId = userId;
		this.tempUserKey = tempUserKey;
		this.exchangeCode = exchangeCode;
		this.exchangeCodeType = exchangeCodeType;
		this.expireTime = expireTime;
	}

	public static JwtExchangeCode tokenIssuable(UUID userId, String exchangeCode, long expireTime) {
		return JwtExchangeCode.builder()
			.id(getExchangeCodeKey(userId))
			.userId(userId)
			.exchangeCode(exchangeCode)
			.expireTime(expireTime)
			.exchangeCodeType(JwtExchangeCodeType.TOKEN_ISSUABLE)
			.build();
	}

	public static JwtExchangeCode signupRequired(String tempUserKey, String exchangeCode, long expireTime) {
		return JwtExchangeCode.builder()
			.id(getExchangeCodeKey(tempUserKey))
			.tempUserKey(tempUserKey)
			.exchangeCode(exchangeCode)
			.expireTime(expireTime)
			.exchangeCodeType(JwtExchangeCodeType.SIGNUP_REQUIRED)
			.build();
	}

}
