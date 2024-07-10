package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findAllByGigId(Integer id);

    List<Enrollment> findAllByBuyerId(Integer id);

    @Query(value = """
                    select count(e) > 0 from Enrollment e\s
                    where e.gig.id = :gigId and e.buyer = :buyer and e.completedAt is not null\s
            """)
    boolean existsByGigIdAndBuyerAndCompletedAtNotNull(Integer gigId, User buyer);
}