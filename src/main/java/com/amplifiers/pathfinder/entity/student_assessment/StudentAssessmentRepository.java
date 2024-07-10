package com.amplifiers.pathfinder.entity.student_assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudentAssessmentRepository extends JpaRepository<StudentAssessment, Integer> {
    Optional<StudentAssessment> findBySession_Id(Integer session_id);
    List<StudentAssessment> findAllBySession_Enrollment_Id(Integer enrollment_id);
}
