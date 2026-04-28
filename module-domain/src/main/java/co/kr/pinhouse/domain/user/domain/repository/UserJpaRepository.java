package co.kr.pinhouse.domain.user.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.kr.pinhouse.domain.user.domain.entity.Provider;
import co.kr.pinhouse.domain.user.domain.entity.User;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID> {
	Optional<User> findByEmail(String email);

	Optional<User> findByProviderAndSocialId(Provider social, String socialId);

	@EntityGraph(attributePaths = "facilityTypes")
		// LAZY 컬렉션을 같이 로딩
	Optional<User> findWithFacilityTypesById(UUID id);

	Page<User> findByNameContainingIgnoreCaseOrNicknameContainingIgnoreCaseOrEmailContainingIgnoreCase(
		String nameKeyword,
		String nicknameKeyword,
		String emailKeyword,
		Pageable pageable
	);

	long countByCreatedAtBetween(LocalDateTime from, LocalDateTime to);
}
