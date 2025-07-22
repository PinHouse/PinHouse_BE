package com.myhome.server.platform.application.out.notice;

import com.myhome.server.platform.domain.notice.Notice;
import java.util.List;

/**
 * DB에 넣는 포트를 정의한 인터페이스입니다.
 * CRUD의 기능을 수행합니다.
 */

public interface NoticePort {

   /// 가져오기
   List<Notice> loadAllNotices();


}
