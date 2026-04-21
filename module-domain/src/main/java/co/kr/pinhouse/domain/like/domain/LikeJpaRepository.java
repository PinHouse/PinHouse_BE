package co.kr.pinhouse.domain.like.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeJpaRepository extends JpaRepository<Like, Long> {

	/// 유저ID, 타입에 맞는 것이 있는지
	boolean existsByUserIdAndTargetIdAndType(UUID userId, String targetId, LikeType type);

	/// 유저 ID와 ID를 바탕으로 한번에 조회
	Optional<Like> findByIdAndUser_Id(Long id, UUID userId);

	/// 유저 ID, targetId, type으로 조회
	Optional<Like> findByUser_IdAndTargetIdAndType(UUID userId, String targetId, LikeType type);

	/// 유저 아이디 바탕으로 조회
	List<Like> findByUser_Id(UUID userId);

	List<Like> findByUser_IdAndType(UUID userId, LikeType type);

	long countByUser_Id(UUID userId);

	void deleteByUser_Id(UUID userId);
}
