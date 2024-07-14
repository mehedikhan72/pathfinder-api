package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/public/gigs")
@RequiredArgsConstructor
public class PublicGigController {
    private final GigService service;
    private final ReviewService reviewService;

    @GetMapping("/all")
    public ResponseEntity<?> findAllGigs() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findGigById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> findGigReviews(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(reviewService.findAllByGigId(id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> findGigsByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(service.findByCategory(category));
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<?> findGigsByQuery(
            @PathVariable String query
    ) {
        return ResponseEntity.ok(service.findByQuery(query));
    }
}
