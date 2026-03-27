package com.pinHouse.domain.search.domain.repository;

import com.pinHouse.domain.search.domain.entity.SearchHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SearchHistoryMongoRepository extends MongoRepository<SearchHistory, String> {
    List<SearchHistory> findByUserId(String userId);
}
