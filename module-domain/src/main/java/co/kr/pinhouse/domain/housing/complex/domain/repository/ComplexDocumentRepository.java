package co.kr.pinhouse.domain.housing.complex.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import co.kr.pinhouse.domain.housing.complex.domain.entity.ComplexDocument;

public interface ComplexDocumentRepository
	extends MongoRepository<ComplexDocument, String>, CustomComplexDocumentRepository {

	List<ComplexDocument> findByNoticeId(String noticeId);

	@Query(value = "{ 'unitTypes.typeId': { $in: ?0 } }",
		fields = "{ 'complexId': 1, 'name': 1, 'unitTypes.$': 1 }")
	List<ComplexDocument> findFirstMatchingUnitType(List<String> typeIds);

	/// 유닛타입 ID 목록으로 단지 조회 (전체 유닛타입 포함)
	@Query("{ 'unitTypes.typeId': { $in: ?0 } }")
	List<ComplexDocument> findComplexesByUnitTypeIds(List<String> typeIds);

	/// 존재하는 모음
	List<ComplexDocument> findByIdIsIn(List<String> complexIds);

	@Query("{ 'location' : { $geoWithin : { $centerSphere: [ [?0, ?1], ?2 ] } } }")
	List<ComplexDocument> findByLocation(double lng, double lat, double radiusInRadians);

}
