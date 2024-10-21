package com.example.elastic.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Document(indexName = "about_awards")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AboutAwardsSearch {

    @Id
    private Long id;

    @Field(name = "title")
    private String title;

    private String url;
}