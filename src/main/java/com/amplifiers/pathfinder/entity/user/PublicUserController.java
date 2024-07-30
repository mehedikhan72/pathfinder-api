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
    public ResponseEntity<UserProfileDTO> getUserProfileData(@PathVariable Integer id, HttpServletRequest request) {
        return ResponseEntity.ok(service.getUserProfileData(id, request));
    }
    @GetMapping("/{id}/profile-image")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(service.getProfileImageDataByUserId(id));
    }
}
