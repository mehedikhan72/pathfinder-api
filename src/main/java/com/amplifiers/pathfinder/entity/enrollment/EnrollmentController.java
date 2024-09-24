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
    private final Integer numEnrollmentsPerPage = Variables.PaginationSettings.NUM_ENROLLMENTS_PER_PAGE;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/{gigId}")
    public ResponseEntity<?> createEnrollment(@RequestBody EnrollmentCreateRequest request, @PathVariable Integer gigId) {
        return ResponseEntity.ok(service.createEnrollment(request, gigId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getEnrollment(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("buyer-confirms/{enrollmentId}")
    public String buyerConfirmsEnrollment(@PathVariable Integer enrollmentId) {
        return service.buyerConfirmsEnrollment(enrollmentId);
    }

    @DeleteMapping("buyer-declines/{enrollmentId}")
    public String buyerDeclinesEnrollment(@PathVariable Integer enrollmentId) {
        service.buyerDeclinesEnrollment(enrollmentId);
        return "declined.";
    }

    @GetMapping("/gig/{gigId}")
    public ResponseEntity<?> findAllByGigId(@RequestParam(name = "page", defaultValue = "0") Integer page, @PathVariable Integer gigId) {
        Pageable pageable = PageRequest.of(page, numEnrollmentsPerPage);
        return ResponseEntity.ok(service.findAllByGigId(pageable, gigId));
    }

    // A user can see all enrollments they have made
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<?> findAllByBuyerId(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @PathVariable Integer buyerId
    ) {
        Pageable pageable = PageRequest.of(page, numEnrollmentsPerPage * 2);
        return ResponseEntity.ok(service.findAllByBuyerId(pageable, buyerId));
    }

    @GetMapping("/deadline-passed/{enrollmentId}")
    public ResponseEntity<?> hasDeadlinePassed(@PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(service.hasDeadlinePassed(enrollmentId));
    }

    @GetMapping("/get/incomplete/seller/{sellerId}/buyer/{buyerId}")
    public ResponseEntity<?> findUnconfirmedBySellerIdAndBuyerId(@PathVariable Integer sellerId, @PathVariable Integer buyerId) {
        return ResponseEntity.ok(service.findIncompleteEnrollmentBySellerIdAndBuyerId(sellerId, buyerId));
    }
    //    @GetMapping("/get/running/{userId1}/{userId2}")
    //    public ResponseEntity<?> findRunningBySellerIdAndBuyerId(
    //            @PathVariable Integer userId1,
    //            @PathVariable Integer userId2
    //    ) {
    //        return ResponseEntity.ok(service.findRunningEnrollmentBetweenTwoUsers(userId1, userId2));
    //    }
}
