package com.healthcare.claims.api.members.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@ConditionalOnProperty(name = "members.features.search.enabled", havingValue = "true")
@Slf4j
public class ElasticsearchIndexService implements SearchIndexService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String esUrl;

    public ElasticsearchIndexService(
            @Value("${members.features.search.elasticsearch-url:http://localhost:9200}") String esUrl) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        this.esUrl = esUrl;
    }

    @Override
    public void indexDocument(String indexName, String documentId, Map<String, Object> document) {
        try {
            String url = esUrl + "/" + indexName + "/_doc/" + documentId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(document), headers);
            restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
            log.info("Indexed document in {}/{}", indexName, documentId);
        } catch (Exception e) {
            log.warn("Failed to index document in {}/{}: {}", indexName, documentId, e.getMessage());
        }
    }

    @Override
    public void updateDocument(String indexName, String documentId, Map<String, Object> document) {
        try {
            String url = esUrl + "/" + indexName + "/_update/" + documentId;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> updateBody = Map.of("doc", document);
            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(updateBody), headers);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            log.info("Updated document in {}/{}", indexName, documentId);
        } catch (Exception e) {
            log.warn("Failed to update document in {}/{}: {}", indexName, documentId, e.getMessage());
        }
    }

    @Override
    public void deleteDocument(String indexName, String documentId) {
        try {
            String url = esUrl + "/" + indexName + "/_doc/" + documentId;
            restTemplate.delete(url);
            log.info("Deleted document from {}/{}", indexName, documentId);
        } catch (Exception e) {
            log.warn("Failed to delete document from {}/{}: {}", indexName, documentId, e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> search(String indexName, String query, int size) {
        try {
            String url = esUrl + "/" + indexName + "/_search";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String searchBody = String.format(
                "{\"query\":{\"multi_match\":{\"query\":\"%s\",\"fields\":[\"*\"]}},\"size\":%d}", query, size);
            HttpEntity<String> entity = new HttpEntity<>(searchBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map body = response.getBody();
            if (body != null && body.containsKey("hits")) {
                Map hits = (Map) body.get("hits");
                List<Map> hitList = (List<Map>) hits.get("hits");
                List<Map<String, Object>> results = new ArrayList<>();
                for (Map hit : hitList) {
                    results.add((Map<String, Object>) hit.get("_source"));
                }
                return results;
            }
        } catch (Exception e) {
            log.warn("Failed to search {}: {}", indexName, e.getMessage());
        }
        return Collections.emptyList();
    }
}
