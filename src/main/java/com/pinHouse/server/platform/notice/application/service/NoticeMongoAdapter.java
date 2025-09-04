package com.pinHouse.server.platform.notice.application.service;

import com.pinHouse.server.platform.notice.domain.entity.NoticeDocument;
import com.pinHouse.server.platform.notice.domain.repository.NoticeDocumentRepository;
import com.pinHouse.server.platform.notice.application.usecase.NoticePort;
import com.pinHouse.server.platform.notice.domain.Notice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Override
    public List<Notice> loadAllNotices() {
        return repository.findAll().stream()
                .map(NoticeDocument::toDomain)
                .toList();
    }

}
