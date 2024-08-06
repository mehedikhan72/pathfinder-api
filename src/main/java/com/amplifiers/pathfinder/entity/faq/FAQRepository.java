package com.amplifiers.pathfinder.entity.faq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FAQRepository extends JpaRepository<FAQ, Integer> {
    List<FAQ> findAllByGigId(Integer id);
}
