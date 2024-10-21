package com.example.elastic.service;

import com.example.elastic.repo.CMSElasticSearchRepository;
import com.example.elastic.utils.ResponseHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CMSElasticSearchService implements CMSElasticSearchRepository {

    private final Map<FieldType, String> FIELD_TYPE_MAP_FOR_ANNOTATED = new HashMap<>();
    private final Map<Class<?>, String> FIELD_TYPE_MAP = new HashMap<>();
    private final ResponseHandler responseHandler;
    @Value("${spring.elasticsearch.rest.uris}")
    private String SEARCH_BASE_URL;

    public CMSElasticSearchService(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        mapConstants();
    }


    @Override
    public void create(Class<?> index) {
        try {
            Map<String, Object> mappings = new HashMap<>();
            Map<String, Object> properties = new HashMap<>();
            Map<String, Object> fields = new HashMap<>();
            String indexName;

            if (index.isAnnotationPresent(Document.class)) {
                Document document = index.getAnnotation(Document.class);
                indexName = document.indexName();
            } else {
                indexName = index.getSimpleName().toLowerCase();
            }
            for (java.lang.reflect.Field field : index.getDeclaredFields()) {
                field.setAccessible(true);

                Map<String, Object> fieldMapping = new HashMap<>();
                if (field.isAnnotationPresent(Field.class)) {
                    Field fieldAnnotation = field.getAnnotation(Field.class);
                    fieldMapping.put("type", FIELD_TYPE_MAP_FOR_ANNOTATED.get(fieldAnnotation.type()));
                }
                fieldMapping.put("type", FIELD_TYPE_MAP.get(field.getType()));
                properties.put(field.getName(), fieldMapping);
            }
            mappings.put("properties", properties);
            fields.put("mappings", mappings);

            ObjectMapper objectMapper = new ObjectMapper();
            String reqMappings = objectMapper.writeValueAsString(fields);
            String url = SEARCH_BASE_URL + "/" + indexName;

            ResponseEntity<String> response = responseHandler.executeRequest(url, HttpMethod.PUT, reqMappings);
            responseHandler.logResponse("Create index", indexName, response);
        } catch (JsonProcessingException ex) {
            log.error("Exception while Creating index ex :{}  message:{}", ex.getClass(), ex.getMessage());

        }
    }

    @Override
    public void save(Object entity, int indexId, String indexName) {
        try {
            Map<String, Object> indexQuery = new HashMap<>();
            Class<?> clazz = entity.getClass();
            for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(entity);

                if (value != null) {
                    indexQuery.put(field.getName(), value);
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String reqMappings = objectMapper.writeValueAsString(indexQuery);
            System.out.println(reqMappings);
            String url = SEARCH_BASE_URL + "/" + indexName + "/_doc/" + indexId;
            System.out.println(url);

            ResponseEntity<String> response = responseHandler.executeRequest(url, HttpMethod.POST, reqMappings);
            responseHandler.logResponse("Save index", indexName, response);
        } catch (IllegalAccessException | JsonProcessingException ex) {
            log.error("Exception while saving index ex :{}  message:{}", ex.getClass(), ex.getMessage());

        }
    }

    @Override
    public void saveAll(List<?> entities) {
        try {
            if (entities.isEmpty()) {
                log.info("Empty Entity Data from DataBase");
                return;
            }

            StringBuilder bulkQuery = new StringBuilder();
            ObjectMapper objectMapper = new ObjectMapper();
            Document document = entities.get(0).getClass().getAnnotation(Document.class);
            String indexName = document.indexName();
            int id = 1;
            for (Object entity : entities) {
                bulkQuery.append("{ \"index\" : { \"_id\" : \"").append(id).append("\" } }\n");

                Map<String, Object> documentMap = new HashMap<>();
                Class<?> clazz = entity.getClass();
                for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = field.get(entity);
                    if (value != null) {
                        documentMap.put(field.getName(), value);
                    }
                }
                String documentJson = objectMapper.writeValueAsString(documentMap);

                bulkQuery.append(documentJson).append("\n");
                id++;
            }
            String url = SEARCH_BASE_URL + "/" + indexName + "/_bulk";
            ResponseEntity<String> response = responseHandler.executeRequest(url, HttpMethod.POST, bulkQuery.toString());
            responseHandler.logResponse("Save All indexes", indexName, response);
        } catch (IllegalAccessException | JsonProcessingException ex) {
            log.error("Exception while saving ALL index ex :{}  message:{}", ex.getClass(), ex.getMessage());
        }
    }


    @Override
    public void delete(Class<?> index) {
        Document document = index.getAnnotation(Document.class);
        String indexName = document.indexName();
        String url = SEARCH_BASE_URL + "/" + indexName + "?ignore_unavailable=true";
        ResponseEntity<String> response = responseHandler.executeRequest(url, HttpMethod.DELETE, null);
        responseHandler.logResponse("Delete index", indexName, response);
    }

    private void mapConstants() {
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Text, "text");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Keyword, "keyword");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Date, "date");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Integer, "integer");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Long, "long");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Double, "double");
        FIELD_TYPE_MAP_FOR_ANNOTATED.put(FieldType.Boolean, "boolean");

        FIELD_TYPE_MAP.put(String.class, "text");
        FIELD_TYPE_MAP.put(Integer.class, "integer");
        FIELD_TYPE_MAP.put(Long.class, "long");
        FIELD_TYPE_MAP.put(Double.class, "double");
        FIELD_TYPE_MAP.put(Float.class, "float");
        FIELD_TYPE_MAP.put(Boolean.class, "boolean");
        FIELD_TYPE_MAP.put(java.util.Date.class, "date");
        FIELD_TYPE_MAP.put(Map.class, "object");
        FIELD_TYPE_MAP.put(Object.class, "object");
        FIELD_TYPE_MAP.put(java.util.List.class, "nested");
    }
}
