package com.healthcare.claims.api.members.search;

import java.util.List;
import java.util.Map;

public interface SearchIndexService {

    void indexDocument(String indexName, String documentId, Map<String, Object> document);

    void updateDocument(String indexName, String documentId, Map<String, Object> document);

    void deleteDocument(String indexName, String documentId);

    List<Map<String, Object>> search(String indexName, String query, int size);
}
