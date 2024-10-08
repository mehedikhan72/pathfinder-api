package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.review.Review;
import com.amplifiers.pathfinder.entity.review.ReviewRequest;
import com.amplifiers.pathfinder.entity.review.ReviewService;
import java.security.Principal;
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
    private final Integer badRequestResponseCode = 400;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createGig(@RequestBody GigCreateRequest request) {
        return ResponseEntity.ok(service.createGig(request));
    }

    @GetMapping("/get/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> findGigById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.privateFindById(id));
    }

    @PatchMapping("/{gigId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> editGig(@PathVariable Integer gigId, @RequestBody GigCreateRequest request) {
        service.editGig(request, gigId);
        return ResponseEntity.ok("Gig id " + gigId + " successfully edited.");
    }

    @PostMapping("/{gigId}/cover-image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setCoverImage(@PathVariable Integer gigId, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.setCoverImage(gigId, file));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(badRequestResponseCode).body(E.getMessage());
        }
    }

    @PostMapping("/{gigId}/gig-video")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setGigVideo(@PathVariable Integer gigId, @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.setGigVideo(gigId, file));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(badRequestResponseCode).body(E.getMessage());
        }
    }

    @GetMapping("/{gigId}/reviews")
    @ResponseStatus(HttpStatus.OK)
    public Review getMyReviewForGig(@PathVariable Integer gigId, Principal connectedUser) {
        return reviewService.getMyReviewForGig(gigId, connectedUser);
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
    public String deleteGig(@PathVariable Integer gigId) {
        return service.deleteGig(gigId);
    }

    // RECOMBEE

    @GetMapping("/recommendations/user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRecommendationsForUser() {
        return ResponseEntity.ok(service.getRecommendationsForUser(null));
    }

    @GetMapping("/recommendations/homepage")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRecommendationsForHomePage() {
        return ResponseEntity.ok(service.getRecommendationsForUser("homepage"));
    }

    @GetMapping("/recommendations/popular-gigs")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getPopularGigsForUser() {
        return ResponseEntity.ok(service.getRecommendationsForUser("popular_gigs"));
    }

    @GetMapping("/recommendations/recently-viewed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getRecentlyViewedForUser() {
        return ResponseEntity.ok(service.getRecommendationsForUser("recently_viewed_gigs"));
    }

    @GetMapping("/recommendations/top-picks")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getTopPicksForUser() {
        return ResponseEntity.ok(service.getRecommendationsForUser("top_picks_for_you"));
    }

    @GetMapping("/recommendations/similar-gigs/{gigId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getSimilarGigsForGig(@PathVariable Integer gigId) {
        return ResponseEntity.ok(service.getRecommendationsForItem(gigId, "similar_gigs"));
    }

    @GetMapping("/next-recommendations/{recommId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getNextRecommendationsForUser(@PathVariable String recommId) {
        return ResponseEntity.ok(service.getNextRecommendationsForUser(recommId));
    }

    @PutMapping("/pause-unpause-gig/{gigId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> pauseUnpauseGig(@PathVariable Integer gigId) {
        return ResponseEntity.ok(service.pauseUnpauseGig(gigId));
    }
}
