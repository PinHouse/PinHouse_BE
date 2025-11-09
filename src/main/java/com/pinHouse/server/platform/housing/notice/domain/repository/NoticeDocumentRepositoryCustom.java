package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

public interface NoticeDocumentRepositoryCustom {

    Page<NoticeDocument> findNoticesByFilters(NoticeListRequest request, Pageable pageable, Instant now);

}
