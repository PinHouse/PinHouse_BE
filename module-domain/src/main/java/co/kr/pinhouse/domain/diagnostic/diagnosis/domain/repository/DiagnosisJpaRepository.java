package co.kr.pinhouse.domain.diagnostic.diagnosis.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.kr.pinhouse.domain.diagnostic.diagnosis.domain.entity.Diagnosis;
import co.kr.pinhouse.domain.user.domain.entity.User;

public interface DiagnosisJpaRepository extends JpaRepository<Diagnosis, Long> {

	/**
	 * 유저 기반으로 진단 탐색하기
	 * @param user  유저
	 */
	Diagnosis findByUser(User user);

	/**
	 * 유저의 최근 진단 1개 조회 (최신순)
	 * @param user  유저
	 * @return 최근 진단
	 */
	Optional<Diagnosis> findTopByUserOrderByCreatedAtDesc(User user);

	/**
	 * 유저 기반으로 모든 진단 히스토리 조회 (최신순)
	 * @param user  유저
	 * @return 진단 목록
	 */
	List<Diagnosis> findAllByUserOrderByCreatedAtDesc(User user);

	long countByUser_Id(UUID userId);

	/**
	 * 유저 ID 기반으로 진단 삭제
	 * @param userId 유저 ID
	 */
	void deleteByUser_Id(UUID userId);

	long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

	@Query("""
		select count(distinct d.user.id)
		from Diagnosis d
		where d.createdAt between :from and :to
		""")
	long countDistinctUsersByCreatedAtBetween(
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to
	);

	@Query("""
		select count(distinct d.user.id)
		from Diagnosis d
		where d.createdAt between :diagnosisFrom and :diagnosisTo
		  and d.user.createdAt between :userFrom and :userTo
		""")
	long countDistinctUsersByCreatedAtBetweenAndUserCreatedAtBetween(
		@Param("diagnosisFrom") LocalDateTime diagnosisFrom,
		@Param("diagnosisTo") LocalDateTime diagnosisTo,
		@Param("userFrom") LocalDateTime userFrom,
		@Param("userTo") LocalDateTime userTo
	);

}
