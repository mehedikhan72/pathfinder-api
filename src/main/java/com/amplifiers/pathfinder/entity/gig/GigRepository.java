package com.amplifiers.pathfinder.entity.gig;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GigRepository extends JpaRepository<Gig, Integer> {
    Optional<Gig> findById(int id);

    Page<Gig> findByCategory(Pageable pageable, String category);

    @Query("SELECT g FROM Gig g LEFT JOIN g.tags t WHERE " +
            "LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Gig> findByQuery(Pageable pageable, @Param("query") String keyword);
}
