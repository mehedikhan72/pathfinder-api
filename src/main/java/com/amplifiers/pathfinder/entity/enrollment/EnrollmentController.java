package com.amplifiers.pathfinder.entity.enrollment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/{gigId}")
    public ResponseEntity<?> createEnrollment(
            @RequestBody EnrollmentCreateRequest request,
            @PathVariable Integer gigId
    ) {
        return ResponseEntity.ok(service.createEnrollment(request, gigId));
    }

    @PutMapping("buyer-confirms/{enrollmentId}")
    public ResponseEntity<?> buyerConfirmsEnrollment(
            @PathVariable Integer enrollmentId
    ) {
        return ResponseEntity.ok(service.buyerConfirmsEnrollment(enrollmentId));
    }

    @GetMapping("/gig/{gigId}")
    public ResponseEntity<?> findAllByGigId(
            @PathVariable Integer gigId
    ) {
        return ResponseEntity.ok(service.findAllByGigId(gigId));
    }

    // A user can see all enrollments they have made
    @GetMapping("/buyer/{buyer_id}")
    public ResponseEntity<?> findAllByBuyerId(
            @PathVariable Integer buyerId
    ) {
        return ResponseEntity.ok(service.findAllByBuyerId(buyerId));
    }

    @GetMapping("/deadline-passed/{enrollmentId}")
    public ResponseEntity<?> hasDeadlinePassed(
            @PathVariable Integer enrollmentId
    ) {
        return ResponseEntity.ok(service.hasDeadlinePassed(enrollmentId));
    }
}
