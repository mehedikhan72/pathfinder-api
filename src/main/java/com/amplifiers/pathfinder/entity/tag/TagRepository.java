package com.amplifiers.pathfinder.entity.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t WHERE " + "LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Tag> findByQuery(String query);
}
