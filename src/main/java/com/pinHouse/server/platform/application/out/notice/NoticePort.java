package com.pinHouse.server.platform.application.out.notice;

import com.pinHouse.server.platform.domain.notice.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * DB에 넣는 포트를 정의한 인터페이스입니다.
 * CRUD의 기능을 수행합니다.
 */

public interface NoticePort {

   /// 가져오기
   Page<Notice> loadNotices(Pageable pageable);

   /// 상세 조회
   Optional<Notice> loadById(String id);

}
