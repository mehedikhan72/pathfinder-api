package com.amplifiers.pathfinder.entity.session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Integer> {
    @Query("""
                select s from Session s
                where s.enrollment.id = :enrollmentId and s.completed = false and s.cancelled = false
            """)
    Optional<Session> findRunningSessionByEnrollmentId(Integer enrollmentId);
}
