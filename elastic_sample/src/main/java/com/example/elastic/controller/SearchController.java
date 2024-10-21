package com.example.elastic.controller;

import com.example.elastic.entity.AboutAwardsSearch;
import com.example.elastic.repo.CMSElasticSearchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("cms")
public class SearchController {


    CMSElasticSearchRepository aboutAwardsSearchRepository;

    public SearchController(CMSElasticSearchRepository aboutAwardsSearchRepository) {
        this.aboutAwardsSearchRepository = aboutAwardsSearchRepository;
    }

    @GetMapping("/create")
    public ResponseEntity<String> search() throws IOException, IllegalAccessException {
        List<AboutAwardsSearch> aboutAwardsSearchList = new ArrayList<>();
        aboutAwardsSearchList.add(new AboutAwardsSearch(1L, "12", "ww"));
        aboutAwardsSearchList.add(new AboutAwardsSearch(2L, "22", "ww"));
        aboutAwardsSearchList.add(new AboutAwardsSearch(3L, "3", "ww"));
        aboutAwardsSearchRepository.create(AboutAwardsSearch.class);
        aboutAwardsSearchRepository.saveAll(aboutAwardsSearchList);
        aboutAwardsSearchRepository.save(new AboutAwardsSearch(4L, "42", "ww"), 4, "about_awards");
        aboutAwardsSearchRepository.delete(AboutAwardsSearch.class);
        return ResponseEntity.ok("");
    }
}