package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.review.ReviewService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.utility.Variables;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.amplifiers.pathfinder.utility.Variables.PaginationSettings.NUM_REVIEWS_PER_PAGE;


@RestController
@RequestMapping("/api/v1/public/gigs")
@RequiredArgsConstructor
public class PublicGigController {
    private final GigService service;
    private final ReviewService reviewService;
    private final UserRepository userRepository;
    Integer numGigsPerPage = Variables.PaginationSettings.NUM_GIGS_PER_PAGE;

    @GetMapping("/all")
    public ResponseEntity<?> findAllGigs(
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, numGigsPerPage);
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findGigById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping("/seller/{userId}")
    public ResponseEntity<?> findGigsByseller(
            @PathVariable Integer userId
    ) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(service.getGigCardsBySeller(user));
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
            @RequestParam(name = "query") String query,
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, numGigsPerPage);
        return ResponseEntity.ok(service.findByQuery(pageable, query));
    }

    // recombee recommendation for anonymous users
    @GetMapping("/recommendations/popular-gigs")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getPopularGigsForUser() {
        return ResponseEntity.ok(service.getRecommendationsForAnonymousUsers("popular_gigs"));
    }
}
