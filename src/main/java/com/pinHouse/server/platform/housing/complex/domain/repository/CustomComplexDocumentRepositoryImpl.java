package com.pinHouse.server.platform.housing.complex.domain.repository;

import com.pinHouse.server.platform.housing.complex.domain.entity.ComplexDocument;
import com.pinHouse.server.platform.housing.notice.application.dto.UnitTypeSortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * ComplexDocument 커스텀 Repository 구현체
 * MongoDB Aggregation Pipeline을 사용한 DB 레벨 정렬 처리
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomComplexDocumentRepositoryImpl implements CustomComplexDocumentRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * 공고에 속한 모든 단지와 유닛타입을 정렬하여 조회
     *
     * MongoDB Aggregation Pipeline을 사용하여:
     * 1. noticeId로 필터링
     * 2. unitTypes 배열을 개별 문서로 펼침 ($unwind)
     * 3. 정렬 기준에 따라 정렬 ($sort with tie-break rules)
     * 4. 단지별로 다시 그룹화 ($group)
     * 5. ComplexDocument 형태로 재구성 ($project)
     *
     * Tie-break 규칙:
     * - 1차: 보증금 or 면적 (정렬 타입에 따라)
     * - 2차: 지역 (address.county)
     * - 3차: 단지명 (name)
     * - 4차: 방 이름 (unitTypes.typeCode)
     *
     * @param noticeId 공고 ID
     * @param sortType 정렬 기준
     * @return 정렬된 단지 목록
     */
    @Override
    public List<ComplexDocument> findSortedComplexesWithUnitTypes(String noticeId, UnitTypeSortType sortType) {

        log.debug("MongoDB Aggregation 시작 - noticeId: {}, sortType: {}", noticeId, sortType);

        // 정렬 기준 설정 (tie-break 규칙 포함)
        Sort sort = buildSortCriteria(sortType);

        // Aggregation Pipeline 구성
        Aggregation aggregation = newAggregation(
                // 1. noticeId로 필터링
                match(Criteria.where("noticeId").is(noticeId)),

                // 2. unitTypes 배열을 개별 문서로 펼침
                unwind("unitTypes"),

                // 3. 정렬 적용 (DB 레벨 정렬)
                sort(sort),

                // 4. 단지별로 다시 그룹화 (⭐ 필드 경로는 $ 접두사 필요)
                group("$_id")
                        .first("$_id").as("_id")
                        .first("$noticeId").as("noticeId")
                        .first("$houseSn").as("houseSn")
                        .first("$name").as("name")
                        .first("$address").as("address")
                        .first("$pnu").as("pnu")
                        .first("$city").as("city")
                        .first("$county").as("county")
                        .first("$heating").as("heating")
                        .first("$totalHouseholds").as("totalHouseholds")
                        .first("$totalSupplyInNotice").as("totalSupplyInNotice")
                        .first("$applyStart").as("applyStart")
                        .first("$applyEnd").as("applyEnd")
                        .first("$location").as("location")
                        .push("$unitTypes").as("unitTypes"),

                // 5. ComplexDocument 형태로 매핑을 위한 projection
                project()
                        .and("_id").as("id")
                        .andInclude(
                                "noticeId", "houseSn", "name", "address",
                                "pnu", "city", "county", "heating",
                                "totalHouseholds", "totalSupplyInNotice",
                                "applyStart", "applyEnd", "location", "unitTypes"
                        )
        );

        // 실행
        AggregationResults<ComplexDocument> results = mongoTemplate.aggregate(
                aggregation,
                "complexes",  // collection name
                ComplexDocument.class
        );

        List<ComplexDocument> complexes = results.getMappedResults();
        log.debug("MongoDB Aggregation 완료 - 조회된 단지 수: {}", complexes.size());

        return complexes;
    }

    /**
     * 정렬 기준 생성 (Tie-break 규칙 포함)
     *
     * 보증금 낮은 순:
     * 1. deposit.total ASC
     * 2. address.county ASC (지역)
     * 3. name ASC (단지명)
     * 4. unitTypes.typeCode ASC (방 이름)
     *
     * 평수 넓은 순:
     * 1. exclusiveAreaM2 DESC
     * 2. address.county ASC (지역)
     * 3. name ASC (단지명)
     * 4. unitTypes.typeCode ASC (방 이름)
     */
    private Sort buildSortCriteria(UnitTypeSortType sortType) {
        if (sortType == UnitTypeSortType.AREA_DESC) {
            // 평수 넓은 순
            return Sort.by(
                    Sort.Order.desc("unitTypes.exclusiveAreaM2"),  // 1차: 면적 큰 순
                    Sort.Order.asc("address.county"),              // 2차: 지역
                    Sort.Order.asc("name"),                        // 3차: 단지명
                    Sort.Order.asc("unitTypes.typeCode")           // 4차: 방 이름
            );
        } else {
            // 보증금 낮은 순 (기본값)
            return Sort.by(
                    Sort.Order.asc("unitTypes.deposit.total"),     // 1차: 보증금 낮은 순
                    Sort.Order.asc("address.county"),              // 2차: 지역
                    Sort.Order.asc("name"),                        // 3차: 단지명
                    Sort.Order.asc("unitTypes.typeCode")           // 4차: 방 이름
            );
        }
    }
}
