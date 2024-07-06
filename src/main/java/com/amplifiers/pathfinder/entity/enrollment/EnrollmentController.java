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

    @GetMapping("/all/{gig_id}")
    public ResponseEntity<?> findAllByGigId(
            @PathVariable Integer gig_id
    ) {
        return ResponseEntity.ok(service.findAllByGigId(gig_id));
    }

    // A user can see all enrollments they have made
    @GetMapping("/{buyer_id}")
    public ResponseEntity<?> findAllByBuyerId(
            @PathVariable Integer buyer_id
    ) {
        return ResponseEntity.ok(service.findAllByBuyerId(buyer_id));
    }
}