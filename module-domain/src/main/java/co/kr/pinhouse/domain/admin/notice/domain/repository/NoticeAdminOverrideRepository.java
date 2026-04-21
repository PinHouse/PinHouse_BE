package co.kr.pinhouse.domain.admin.notice.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.kr.pinhouse.domain.admin.notice.domain.entity.NoticeAdminOverride;

public interface NoticeAdminOverrideRepository extends JpaRepository<NoticeAdminOverride, Long> {

	Optional<NoticeAdminOverride> findByNoticeId(String noticeId);

	List<NoticeAdminOverride> findByNoticeIdIn(Collection<String> noticeIds);
}
