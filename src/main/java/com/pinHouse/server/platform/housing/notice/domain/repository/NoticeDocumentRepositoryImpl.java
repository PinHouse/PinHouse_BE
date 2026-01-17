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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RequiredArgsConstructor
public class NoticeDocumentRepositoryImpl implements NoticeDocumentRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<NoticeDocument> findNoticesByFilters(NoticeListRequest request, Pageable pageable, Instant now) {
        Criteria criteria = new Criteria();

        /// 모집중 필터링을 위한 오늘의 시작 시각 계산 (오늘까지 포함되도록)
        Instant todayStart = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"))
                .toLocalDate()
                .atStartOfDay(ZoneId.of("Asia/Seoul"))
                .toInstant();

        /// 모집 상태 필터링 (현재 날짜 기준)
        boolean applyEndFiltered = false;

        /// 모집 상태 필터링 (현재 날짜 기준)
        if (request.status() != null) {
            if (request.status() == NoticeListRequest.NoticeStatus.RECRUITING) {
                criteria.and("applyEnd").gte(todayStart);
                applyEndFiltered = true;
            }
            // ALL이면 아무 조건 X
        }

        /// 시간 기준 정렬 (최신/마감임박)
        if (request.sortType() == NoticeListRequest.ListSortType.END) {
            // 이미 applyEnd 조건이 있다면 다시 넣지 않음
            if (!applyEndFiltered) {
                criteria.and("applyEnd").gte(todayStart);
                applyEndFiltered = true;
            }
        } else {
            criteria.and("announceDate").lte(now);
        }

        /// 지역 필터링
        if (request.regionType() != null && !request.regionType().isEmpty()) {
            List<String> cities = request.regionType().stream()
                    .map(NoticeListRequest.Region::getFullName)
                    .toList();
            criteria.and("city").in(cities);
        }

        /// 모집 대상 필터링
        if (request.rentalTypes() != null && !request.rentalTypes().isEmpty()) {
            List<String> targetDisplayNames = request.rentalTypes().stream()
                    .map(NoticeListRequest.TargetType::getDisplayName)
                    .toList();
            criteria.and("targetGroup").in(targetDisplayNames);
        }

        /// 임대 유형 필터링
        if (request.supplyTypes() != null && !request.supplyTypes().isEmpty()) {
            List<String> leaseTypes = request.supplyTypes().stream()
                    .map(NoticeListRequest.LeaseType::getDisplayName)
                    .toList();
            criteria.and("supplyType").in(leaseTypes);
        }

        /// 주택 유형 필터링
        if (request.houseTypes() != null && !request.houseTypes().isEmpty()) {
            List<String> houseTypes = request.houseTypes().stream()
                    .map(NoticeListRequest.HouseType::getDisplayName)
                    .toList();
            criteria.and("houseType").in(houseTypes);
        }

        Query query = new Query(criteria).with(pageable);

        /// 실행 및 Page 응답 구성
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), NoticeDocument.class);

        return new PageImpl<>(notices, pageable, count);
    }

    @Override
    public Page<NoticeDocument> searchByTitle(String keyword, Pageable pageable, boolean filterOpen, Instant now) {
        // 부분 문자열 검색을 위한 regex 사용 (대소문자 무시)
        Criteria criteria = Criteria.where("title").regex(keyword, "i");

        Query query = new Query(criteria).with(pageable);

        // 모집중 필터 적용 (오늘까지 포함되도록)
        if (filterOpen) {
            Instant todayStart = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"))
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("Asia/Seoul"))
                    .toInstant();
            query.addCriteria(Criteria.where("applyEnd").gte(todayStart));
        }

        /// 실행 및 Page 응답 구성
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), NoticeDocument.class);

        return new PageImpl<>(notices, pageable, count);
    }

    @Override
    public org.springframework.data.domain.Slice<NoticeDocument> searchByTitleSlice(String keyword, Pageable pageable, boolean filterOpen, Instant now) {
        // 부분 문자열 검색을 위한 regex 사용 (대소문자 무시)
        Criteria criteria = Criteria.where("title").regex(keyword, "i");

        Query query = new Query(criteria).with(pageable);

        // 모집중 필터 적용 (오늘까지 포함되도록)
        if (filterOpen) {
            Instant todayStart = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"))
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("Asia/Seoul"))
                    .toInstant();
            query.addCriteria(Criteria.where("applyEnd").gte(todayStart));
        }

        // 무한 스크롤을 위해 limit + 1 조회
        int limit = pageable.getPageSize();
        query.limit(limit + 1);

        /// 실행
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);

        // hasNext 판별: limit+1개 조회했을 때, limit+1번째가 있으면 다음 페이지가 있음
        boolean hasNext = notices.size() > limit;

        // 실제 반환 데이터는 limit개만
        List<NoticeDocument> content = hasNext ? notices.subList(0, limit) : notices;

        return new org.springframework.data.domain.SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public long countByTitle(String keyword, boolean filterOpen, Instant now) {
        // 부분 문자열 검색을 위한 regex 사용 (대소문자 무시)
        Criteria criteria = Criteria.where("title").regex(keyword, "i");

        Query query = new Query(criteria);

        // 모집중 필터 적용 (오늘까지 포함되도록)
        if (filterOpen) {
            Instant todayStart = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"))
                    .toLocalDate()
                    .atStartOfDay(ZoneId.of("Asia/Seoul"))
                    .toInstant();
            query.addCriteria(Criteria.where("applyEnd").gte(todayStart));
        }

        return mongoTemplate.count(query, NoticeDocument.class);
    }

    @Override
    public Page<NoticeDocument> findDeadlineApproachingNoticesByRegionAndCounty(
            String region,
            String county,
            Pageable pageable,
            Instant now
    ) {
        Criteria criteria = new Criteria();

        /// 오늘의 시작 시각 계산 (오늘까지 포함되도록)
        Instant todayStart = ZonedDateTime.ofInstant(now, ZoneId.of("Asia/Seoul"))
                .toLocalDate()
                .atStartOfDay(ZoneId.of("Asia/Seoul"))
                .toInstant();

        /// 모집중인 공고만 조회 (마감일이 오늘 이후)
        criteria.and("applyEnd").gte(todayStart);

        /// 광역 단위 필터링
        if (region != null && !region.isBlank()) {
            criteria.and("city").is(region);
        }

        /// 시/군/구 필터링 (부분 일치: "성남시"로 "성남시 수정구", "성남시 분당구" 등 매칭)
        if (county != null && !county.isBlank()) {
            criteria.and("county").regex("^" + county);
        }

        Query query = new Query(criteria).with(pageable);

        /// 실행 및 Page 응답 구성
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), NoticeDocument.class);

        return new PageImpl<>(notices, pageable, count);
    }

    @Override
    public Page<NoticeDocument> findRecommendedNoticesByDiagnosis(
            java.util.List<String> supplyTypes,
            Pageable pageable
    ) {
        Criteria criteria = new Criteria();

        /// supplyType 필터링 (진단 결과에서 매핑된 주택 유형)
        if (supplyTypes != null && !supplyTypes.isEmpty()) {
            criteria.and("supplyType").in(supplyTypes);
        }

        /// 모집 상태 필터링 없음 (모든 공고 포함)
        /// 정렬은 Pageable의 Sort로 처리 (마감임박순 권장)

        Query query = new Query(criteria).with(pageable);

        /// 실행 및 Page 응답 구성
        List<NoticeDocument> notices = mongoTemplate.find(query, NoticeDocument.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), NoticeDocument.class);

        return new PageImpl<>(notices, pageable, count);
    }

}
