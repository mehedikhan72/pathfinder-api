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
    @PostMapping("/create/{gig_id}")
    public ResponseEntity<?> createEnrollment(
            @RequestBody EnrollmentCreateRequest request,
            @PathVariable Integer gig_id
    ) {
        return ResponseEntity.ok(service.createEnrollment(request, gig_id));
    }

    @PutMapping("buyer-confirms/{enrollment_id}")
    public ResponseEntity<?> buyerConfirmsEnrollment(
            @PathVariable Integer enrollment_id
    ) {
        return ResponseEntity.ok(service.buyerConfirmsEnrollment(enrollment_id));
    }

    @GetMapping("/gig/{gig_id}")
    public ResponseEntity<?> findAllByGigId(
            @PathVariable Integer gig_id
    ) {
        return ResponseEntity.ok(service.findAllByGigId(gig_id));
    }

    // A user can see all enrollments they have made
    @GetMapping("/buyer/{buyer_id}")
    public ResponseEntity<?> findAllByBuyerId(
            @PathVariable Integer buyer_id
    ) {
        return ResponseEntity.ok(service.findAllByBuyerId(buyer_id));
    }

    @GetMapping("/deadline-passed/{enrollment_id}")
    public ResponseEntity<?> hasDeadlinePassed(
            @PathVariable Integer enrollment_id
    ) {
        return ResponseEntity.ok(service.hasDeadlinePassed(enrollment_id));
    }
}
