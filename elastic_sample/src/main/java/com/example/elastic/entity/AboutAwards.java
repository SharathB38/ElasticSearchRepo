package com.example.elastic.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "about_awards")
@Entity
public class AboutAwards {

    @Id
    private Long id;

    @Column(name = "title")
    private String title;

    private int url;
}