package com.amplifiers.pathfinder.entity.review;

import com.amplifiers.pathfinder.entity.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Page<Review> findAllByGigId(Pageable pageable, Integer gigId);

    List<Review> findAllByGigId(Integer gigId);

    @Query(
        """
            select r.rating from Review r left join Gig g on r.gig = g
            where g.seller.id = :sellerId
        """
    )
    List<Float> findAllRatingsBySellerId(Integer sellerId);

    @Query(
        """
            select r from Review r left join Gig g on r.gig = g
            where g.seller.id = :sellerId
        """
    )
    Page<Review> findAllReviewsBySellerId(Pageable pageable, Integer sellerId);

    boolean existsByIdAndGigId(Integer id, Integer gigId);

    boolean existsByIdAndReviewer(Integer id, User reviewer);

    Optional<Review> findByGigIdAndAndReviewer(Integer gigId, User reviewer);
}
