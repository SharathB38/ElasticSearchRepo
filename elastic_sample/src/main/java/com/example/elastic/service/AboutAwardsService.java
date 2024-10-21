package com.example.elastic.service;

import com.example.elastic.entity.AboutAwards;
import com.example.elastic.entity.AboutAwardsSearch;
import com.example.elastic.repo.AboutAwardsRepository;
import com.example.elastic.repo.CMSElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AboutAwardsService {

    @Autowired
    private AboutAwardsRepository aboutAwardsRepository;

    @Autowired
    private CMSElasticSearchRepository cmsElasticSearchRepository;

    public void parse() {

        cmsElasticSearchRepository.delete(AboutAwardsSearch.class);
        cmsElasticSearchRepository.create(AboutAwardsSearch.class);
        List<AboutAwards> aboutAwards = aboutAwardsRepository.findAll();
        List<AboutAwardsSearch> aboutAwardsSearchList = aboutAwards.stream().map(aboutAward -> {
                    AboutAwardsSearch aboutAwardsSearch = new AboutAwardsSearch();
                    aboutAwardsSearch.setId(aboutAward.getId());
                    aboutAwardsSearch.setTitle(aboutAward.getTitle());
                    return aboutAwardsSearch;
                }
        ).toList();
        cmsElasticSearchRepository.saveAll(aboutAwardsSearchList);

    }
}
