package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Integer> {


    public List<Review> findAllByGigId(Integer gigId);
    public boolean existsByIdAndGigId(Integer id, Integer gigId);
    public boolean existsByIdAndReviewer(Integer id, User reviewer);
}
