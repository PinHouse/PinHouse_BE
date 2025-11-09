package com.pinHouse.server.platform.housing.notice.domain.repository;

import com.pinHouse.server.platform.housing.notice.application.dto.NoticeListRequest;
import com.pinHouse.server.platform.housing.notice.domain.entity.NoticeDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class NoticeDocumentRepositoryImpl implements NoticeDocumentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<NoticeDocument> findNoticesByFilters(NoticeListRequest request, Pageable pageable, Instant now) {
        Criteria criteria = new Criteria();

        /// 시간 기준 필터링 (최신/마감임박)
        if (request.sortType() == NoticeListRequest.ListSortType.END) {
            criteria.and("applyEnd").gte(now);
        } else {
            criteria.and("announceDate").lte(now);
        }

        /// 지역 필터링
        if (request.regionType() != null && !request.regionType().isEmpty()) {
            List<String> cities = request.regionType().stream().map(NoticeListRequest.Region::getFullName).toList();
            criteria.and("city").in(cities);
        }

        /// 타입 유형 필터링
        if (request.targetType() != null && !request.targetType().isEmpty()) {
            List<String> targetDisplayNames = request.targetType().stream()
                    .map(NoticeListRequest.TargetType::getDisplayName)
                    .toList();

            criteria.and("targetGroup").in(targetDisplayNames);
        }
        /// 임대 유형 필터링
        if (request.leaseType() != null && !request.leaseType().isEmpty()) {
            List<String> leaseTypes = request.leaseType().stream().map(NoticeListRequest.LeaseType::getDisplayName).toList();
            criteria.and("supplyType").in(leaseTypes);
        }

        /// 주택 유형 필터링
        if (request.houseTypes() != null && !request.houseTypes().isEmpty()) {
            List<String> houseTypes = request.houseTypes().stream().map(NoticeListRequest.HouseType::getDisplayName).toList();
            criteria.and("houseType").in(houseTypes);
        }

        Query query = new Query(criteria).with(pageable);

        /// 실행 및 Page/Slice 응답 구성
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);

        // 전체 카운트
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), NoticeDocument.class);

        return new PageImpl<>(notices, pageable, count);
    }
}
