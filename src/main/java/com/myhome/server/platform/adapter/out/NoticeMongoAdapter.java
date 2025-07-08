package com.myhome.server.platform.adapter.out;

import com.myhome.server.platform.adapter.out.mongo.notice.NoticeDocument;
import com.myhome.server.platform.adapter.out.mongo.notice.NoticeDocumentRepository;
import com.myhome.server.platform.application.out.notice.NoticePort;
import com.myhome.server.platform.domain.notice.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * NoticePort의 구현체입니다.
 * Repository를 주입받아 역할을 수행합니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeMongoAdapter implements NoticePort {

    private final NoticeDocumentRepository repository;

    @Override
    public Page<Notice> loadNotices(Pageable pageable) {
        return repository.findAll(pageable)
                .map(NoticeDocument::toDomain);
    }

    @Override
    public Optional<Notice> loadById(String noticeId) {
        return repository.findByNoticeId(noticeId)
                .map(NoticeDocument::toDomain);
    }
}
