package com.example.elastic.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ResponseHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

    @Value("${spring.elasticsearch.rest.password}")
    private String password;

    public <T> ResponseEntity<String> executeRequest(String url, HttpMethod method, String body) {
        HttpEntity<String> requestEntity = new HttpEntity<>(body, getHeaders());
        log.info("Executing request: URL: {}, Method: {}, Headers: {}", url, method, requestEntity.getHeaders());
        if (body != null && !body.isEmpty()) {
            log.info("Request body: {}", body);
        }

        ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);

        log.info("Response status: {}", response.getStatusCode());
        log.info("Response headers: {}", response.getHeaders());
        log.info("Response body: {}", response.getBody());
        return response;
    }

    public void logResponse(String operation, String indexName, ResponseEntity<String> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("{} operation completed successfully for index {}", operation, indexName);
        } else {
            log.error("{} operation failed with status: {}, Response body: {}", operation, response.getStatusCode(), response.getBody());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-ndjson");
        return headers;
    }
}
