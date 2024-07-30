package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GigRepository extends JpaRepository<Gig, Integer> {
    Optional<Gig> findById(int id);
    List<Gig> findByCategory(String category);

    List<Gig> findAllBySeller(User seller);
    @Query("SELECT g FROM Gig g LEFT JOIN g.tags t WHERE " +
            "LOWER(g.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Gig> findByQuery(@Param("query") String keyword);
}
