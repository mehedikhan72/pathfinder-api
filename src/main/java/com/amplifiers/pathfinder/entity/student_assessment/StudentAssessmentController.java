package com.amplifiers.pathfinder.entity.student_assessment;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/student_assessments")
@RequiredArgsConstructor
public class StudentAssessmentController {
    private final StudentAssessmentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/{session_id}")
    public ResponseEntity<?> createStudentAssessment(
            @RequestBody StudentAssessmentCreateRequest request,
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.createStudentAssessment(request, session_id));
    }

    @GetMapping("/session/{session_id}")
    public ResponseEntity<?> getStudentAssessmentForASession(
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.getStudentAssessmentForASession(session_id));
    }

    @GetMapping("/enrollment/{enrollment_id}")
    public ResponseEntity<?> getAllStudentAssessmentForAnEnrollment(
            @PathVariable Integer enrollment_id
    ) {
        return ResponseEntity.ok(service.getAllStudentAssessmentForAnEnrollment(enrollment_id));
    }
}
