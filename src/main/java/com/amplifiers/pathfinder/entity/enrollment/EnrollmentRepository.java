package com.amplifiers.pathfinder.entity.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {
    List<Enrollment> findAllByGigId(Integer id);
    List<Enrollment> findAllByBuyerId(Integer id);
}