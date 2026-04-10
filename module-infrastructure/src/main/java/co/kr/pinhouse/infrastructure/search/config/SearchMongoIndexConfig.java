package co.kr.pinhouse.infrastructure.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

import co.kr.pinhouse.domain.housing.notice.domain.entity.NoticeDocument;
import co.kr.pinhouse.domain.search.domain.entity.SearchKeyword;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SearchMongoIndexConfig {

	private final MongoTemplate mongoTemplate;

	@PostConstruct
	public void initIndexes() {
		createNoticeTextIndex();
		createSearchKeywordIndexes();
		log.info("MongoDB search indexes initialized successfully");
	}

	private void createNoticeTextIndex() {
		try {
			TextIndexDefinition textIndex = TextIndexDefinition.builder()
				.onField("title")
				.build();

			mongoTemplate.indexOps(NoticeDocument.class)
				.ensureIndex(textIndex);

			log.info("Created text index on NoticeDocument.title");
		} catch (Exception e) {
			log.error("Failed to create text index on NoticeDocument.title", e);
		}
	}

	private void createSearchKeywordIndexes() {
		try {
			Index keywordIndex = new Index()
				.named("idx_keyword")
				.on("keyword", Sort.Direction.ASC)
				.unique();

			mongoTemplate.indexOps(SearchKeyword.class)
				.ensureIndex(keywordIndex);

			Index countIndex = new Index()
				.named("idx_count")
				.on("count", Sort.Direction.DESC);

			mongoTemplate.indexOps(SearchKeyword.class)
				.ensureIndex(countIndex);

			Index lastSearchedIndex = new Index()
				.named("idx_last_searched")
				.on("lastSearchedAt", Sort.Direction.DESC);

			mongoTemplate.indexOps(SearchKeyword.class)
				.ensureIndex(lastSearchedIndex);

			Index compoundIndex = new Index()
				.named("idx_popular")
				.on("count", Sort.Direction.DESC)
				.on("lastSearchedAt", Sort.Direction.DESC);

			mongoTemplate.indexOps(SearchKeyword.class)
				.ensureIndex(compoundIndex);

			log.info("Created indexes on SearchKeyword collection");
		} catch (Exception e) {
			log.error("Failed to create indexes on SearchKeyword collection", e);
		}
	}
}
