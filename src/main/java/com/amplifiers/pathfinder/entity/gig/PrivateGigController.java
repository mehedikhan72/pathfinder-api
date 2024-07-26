package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.review.Review;
import com.amplifiers.pathfinder.entity.review.ReviewRequest;
import com.amplifiers.pathfinder.entity.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/v1/gigs")
@RequiredArgsConstructor
public class PrivateGigController {
    private final GigService service;
    private final ReviewService reviewService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createGig(
            @RequestBody GigCreateRequest request
    ) {
        return ResponseEntity.ok(service.createGig(request));
    }

    @PostMapping("/{gigId}/cover-image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setCoverImage(@PathVariable Integer gigId, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.setCoverImage(gigId, file));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }
    @PostMapping("/{gigId}/gig-video")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setGigVideo(@PathVariable Integer gigId, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.setGigVideo(gigId, file));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

    @PostMapping("/{gigId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(@PathVariable Integer gigId, @RequestBody ReviewRequest reviewRequest) {
        return reviewService.createReview(gigId, reviewRequest);
    }

    @PutMapping("/{gigId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public Review editReview(@PathVariable Integer gigId, @PathVariable Integer reviewId, @RequestBody ReviewRequest reviewRequest) {
        return reviewService.editReview(gigId, reviewId, reviewRequest);
    }

    @DeleteMapping("/{gigId}/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public String deleteReview(@PathVariable Integer gigId, @PathVariable Integer reviewId) {
        reviewService.deleteReview(gigId, reviewId);
        return "Review id " + reviewId + " successfully deleted.";
    }

    @DeleteMapping("/delete/{gigId}")
    public String deleteGig(
            @PathVariable Integer gigId
    ) {
        return service.deleteGig(gigId);
    }
}
