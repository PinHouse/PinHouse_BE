package co.kr.pinhouse.domain.search.domain.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.kr.pinhouse.domain.search.domain.entity.SearchHistory;

public interface SearchHistoryMongoRepository extends MongoRepository<SearchHistory, String> {
	List<SearchHistory> findByUserId(String userId);
}
