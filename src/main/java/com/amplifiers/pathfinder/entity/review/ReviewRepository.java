package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {


    List<Review> findAllByGigId(Integer gigId);

    boolean existsByIdAndGigId(Integer id, Integer gigId);

    boolean existsByIdAndReviewer(Integer id, User reviewer);
}
