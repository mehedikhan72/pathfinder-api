package com.amplifiers.pathfinder.entity.gig;

import static com.amplifiers.pathfinder.utility.Variables.PaginationSettings.NUM_REVIEWS_PER_PAGE;

import com.amplifiers.pathfinder.entity.review.ReviewService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.utility.Category;
import com.amplifiers.pathfinder.utility.Variables;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/gigs")
@RequiredArgsConstructor
public class PublicGigController {

    private final GigService service;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    private Integer numGigsPerPage = Variables.PaginationSettings.NUM_GIGS_PER_PAGE;

    @GetMapping("/all")
    public ResponseEntity<?> findAllGigs(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        Pageable pageable = PageRequest.of(page, numGigsPerPage);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findGigById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/seller/{userId}")
    public ResponseEntity<?> findGigsBySeller(@PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(service.getPublicGigCardsBySeller(user));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> findGigReviews(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
        @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction order,
        @PathVariable Integer id
    ) {
        Pageable pageable = PageRequest.of(page, NUM_REVIEWS_PER_PAGE, Sort.by(order, sort));
        return ResponseEntity.ok(reviewService.findAllCardsByGigId(pageable, id));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> findGigsByCategory(
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @PathVariable String category
    ) {
        Pageable pageable = PageRequest.of(page, numGigsPerPage);
        return ResponseEntity.ok(service.findByCategory(pageable, category));
    }

    @GetMapping("/search")
    public ResponseEntity<?> findGigsByQuery(
        @RequestParam(name = "query", defaultValue = "") String query,
        @RequestParam(name = "page", defaultValue = "0") Integer page,
        @RequestParam(name = "ratingAbove", defaultValue = "0") Float ratingAbove,
        @RequestParam(name = "budget", required = false) Float budget,
        @RequestParam(name = "category", required = false) String category,
        @RequestParam(name = "tags", required = false) List<String> tags,
        @RequestParam(name = "sort", defaultValue = "score") String sort,
        @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction order
    ) {
        Pageable pageable = PageRequest.of(page, numGigsPerPage, Sort.by(order, sort));
        return ResponseEntity.ok(service.findByQuery(pageable, query, ratingAbove, budget, Category.fromString(category), tags));
    }

    // recombee recommendation for anonymous users
    @GetMapping("/recommendations/popular-gigs")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getPopularGigsForUser() {
        return ResponseEntity.ok(service.getRecommendationsForAnonymousUsers("popular_gigs"));
    }
}
