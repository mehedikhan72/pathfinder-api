package com.amplifiers.pathfinder.entity.enrollment;

import com.amplifiers.pathfinder.utility.Variables;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService service;
    Integer numEnrollmentsPerPage = Variables.PaginationSettings.NUM_ENROLLMENTS_PER_PAGE;

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
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @PathVariable Integer gigId
    ) {
        Pageable pageable = PageRequest.of(page, numEnrollmentsPerPage);
        return ResponseEntity.ok(service.findAllByGigId(pageable, gigId));
    }

    // A user can see all enrollments they have made
    @GetMapping("/buyer/{buyer_id}")
    public ResponseEntity<?> findAllByBuyerId(
            @RequestParam(name="page", defaultValue = "0") Integer page,
            @PathVariable Integer buyerId
    ) {
        Pageable pageable = PageRequest.of(page, numEnrollmentsPerPage);
        return ResponseEntity.ok(service.findAllByBuyerId(pageable, buyerId));
    }

    @GetMapping("/deadline-passed/{enrollmentId}")
    public ResponseEntity<?> hasDeadlinePassed(
            @PathVariable Integer enrollmentId
    ) {
        return ResponseEntity.ok(service.hasDeadlinePassed(enrollmentId));
    }
}
