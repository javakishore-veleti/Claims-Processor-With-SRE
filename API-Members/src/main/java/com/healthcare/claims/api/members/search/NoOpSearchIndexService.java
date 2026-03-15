package com.healthcare.claims.api.members.search;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@ConditionalOnMissingBean(ElasticsearchIndexService.class)
@Slf4j
public class NoOpSearchIndexService implements SearchIndexService {

    @Override
    public void indexDocument(String indexName, String documentId, Map<String, Object> document) {
        log.debug("Search indexing is disabled. Skipping indexDocument for index={}, id={}", indexName, documentId);
    }

    @Override
    public void updateDocument(String indexName, String documentId, Map<String, Object> document) {
        log.debug("Search indexing is disabled. Skipping updateDocument for index={}, id={}", indexName, documentId);
    }

    @Override
    public void deleteDocument(String indexName, String documentId) {
        log.debug("Search indexing is disabled. Skipping deleteDocument for index={}, id={}", indexName, documentId);
    }

    @Override
    public List<Map<String, Object>> search(String indexName, String query, int size) {
        log.debug("Search indexing is disabled. Skipping search for index={}, query={}", indexName, query);
        return Collections.emptyList();
    }
}
