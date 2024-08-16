package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.gig.Gig;
import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    Page<Enrollment> findAllByGigId(Pageable pageable, Integer id);

    Page<Enrollment> findAllByBuyerId(Pageable pageable, Integer id);

    // INFO - two users will have only one enrollment at a time. Need to handle this in the service(enrollment creation)

    @Query("""
                select e from Enrollment e left join Gig g on e.gig = g
                where g.seller.id = :sellerId and e.buyer.id = :buyerId and e.completedAt is null
            """)
    Optional<Enrollment> findIncompleteEnrollmentBySellerIdAndBuyerId(Integer sellerId, Integer buyerId);

    // But this function returns a list cause the same users could possibly have completed an enrollment in the past.
    @Query("""
                select e from Enrollment e left join Gig g on e.gig = g
                where g.seller.id = :sellerId and e.buyer.id = :buyerId
            """)
    List<Enrollment> findEnrollmentsBySellerIdAndBuyerId(Integer sellerId, Integer buyerId);

    @Query("""
                select count(e) from Enrollment e left join Gig g on e.gig = g
                where g.seller.id = :sellerId and e.completedAt is not null
            """)
    Integer countCompletedBySellerId(Integer sellerId);

    @Query("""
                select count(distinct e.buyer) from Enrollment e left join Gig g on e.gig = g
                where g.seller.id = :sellerId
            """)
    Integer countDistinctStudentsBySellerId(Integer sellerId);

    Integer countByGig(Gig gig);

    Integer countByGigAndCompletedAtNotNull(Gig gig);

    @Query(value = """
                    select count(e) > 0 from Enrollment e\s
                    where e.gig.id = :gigId and e.buyer = :buyer and e.completedAt is not null\s
            """)
    boolean existsByGigIdAndBuyerAndCompletedAtNotNull(Integer gigId, User buyer);
}