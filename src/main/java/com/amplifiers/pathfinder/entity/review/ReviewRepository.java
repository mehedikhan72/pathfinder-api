package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findAllByGigId(Integer gigId);

    @Query("""
                select r.rating from Review r left join Gig g on r.gig = g
                where g.seller.id = :sellerId
            """)
    List<Float> findAllRatingsBySellerId(Integer sellerId);

    @Query("""
                select r from Review r left join Gig g on r.gig = g
                where g.seller.id = :sellerId
            """)
    List<Review> findAllReviewsBySellerId(Integer sellerId);

    boolean existsByIdAndGigId(Integer id, Integer gigId);

    boolean existsByIdAndReviewer(Integer id, User reviewer);
}
