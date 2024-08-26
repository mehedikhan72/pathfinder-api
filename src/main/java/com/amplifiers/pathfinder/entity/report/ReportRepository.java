package com.amplifiers.pathfinder.entity.report;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface ReportRepository extends JpaRepository<Report, Integer> {
    Page<Report> findAllByResolvedFalseOrderByCreatedAtAsc(Pageable pageable);
    Page<Report> findAllByResolvedTrueOrderByResolvedAtAsc(Pageable pageable);
}
