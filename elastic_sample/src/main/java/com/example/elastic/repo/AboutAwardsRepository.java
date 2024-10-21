package com.example.elastic.repo;

import com.example.elastic.entity.AboutAwards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AboutAwardsRepository extends JpaRepository<AboutAwards, Long> {
}
