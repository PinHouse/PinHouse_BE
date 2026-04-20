package co.kr.pinhouse.security.jwt.domain.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import co.kr.pinhouse.security.jwt.domain.entity.JwtExchangeCode;

public interface JwtExchangeCodeRepository extends CrudRepository<JwtExchangeCode, String> {

	/// Exchange code로 조회
	Optional<JwtExchangeCode> findByExchangeCode(String exchangeCode);
}
