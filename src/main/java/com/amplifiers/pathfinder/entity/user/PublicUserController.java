package com.amplifiers.pathfinder.entity.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.amplifiers.pathfinder.utility.Variables.PaginationSettings.NUM_REVIEWS_PER_PAGE;

@RestController
@RequestMapping("/api/v1/public/users")
@RequiredArgsConstructor
public class PublicUserController {
    private final UserService service;

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfileData(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getUserProfileData(id));
    }

    @GetMapping("/{id}/profile-image")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(service.getProfileImageDataByUserId(id));
    }

    @GetMapping("/{id}/gigs")
    public ResponseEntity<?> getGigs(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getGigs(id));
    }

    @GetMapping("/{id}/gigs/card")
    public ResponseEntity<?> getGigCards(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getGigCards(id));
    }

    @GetMapping("/{id}/reviews/card")
    public ResponseEntity<?> getReviewCards(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "order", defaultValue = "DESC") Sort.Direction order,
            @PathVariable Integer id) {
        Pageable pageable = PageRequest.of(page, NUM_REVIEWS_PER_PAGE, Sort.by(order, sort));
        return ResponseEntity.ok(service.getReviewCards(pageable, id));
    }
}
