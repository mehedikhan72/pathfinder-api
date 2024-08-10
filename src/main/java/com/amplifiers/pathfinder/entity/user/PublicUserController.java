package com.amplifiers.pathfinder.entity.user;

import com.amplifiers.pathfinder.cloudstorage.CloudStorageService;
import com.amplifiers.pathfinder.entity.image.Image;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{id}/gigs/")
    public ResponseEntity<?> getGigs(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getGigs(id));
    }

    @GetMapping("/{id}/gigs/card")
    public ResponseEntity<?> getGigCards(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getGigCards(id));
    }

    @GetMapping("/{id}/reviews/card")
    public ResponseEntity<?> getReviewCards(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getReviewCards(id));
    }
}
