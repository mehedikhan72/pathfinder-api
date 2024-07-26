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
    @PostMapping("/create/{sessionId}")
    public ResponseEntity<?> createStudentAssessment(
            @RequestBody StudentAssessmentCreateRequest request,
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.createStudentAssessment(request, sessionId));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getStudentAssessmentForASession(
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.getStudentAssessmentForASession(sessionId));
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<?> getAllStudentAssessmentForAnEnrollment(
            @PathVariable Integer enrollmentId
    ) {
        return ResponseEntity.ok(service.getAllStudentAssessmentForAnEnrollment(enrollmentId));
    }
}
