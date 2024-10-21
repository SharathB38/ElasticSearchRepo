package com.example.elastic.repo;

import java.util.List;


public interface CMSElasticSearchRepository {

    void create(Class<?> clazz);

    void save(Object entity, int indexId, String indexName);

    void delete(Class<?> clazz);

    void saveAll(List<?> entities);

}
