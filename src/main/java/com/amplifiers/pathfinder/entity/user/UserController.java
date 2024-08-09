package com.amplifiers.pathfinder.entity.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PatchMapping
    public ResponseEntity<?> changePassword(
          @RequestBody ChangePasswordRequest request,
          Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/edit-profile")
    public ResponseEntity<?> editProfile(
            @RequestBody ProfileEditRequest request,
            Principal connectedUser
    ) {
        service.editProfile(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile-image")
    @ResponseStatus(HttpStatus.OK)
    public String setProfileImage(
            @RequestParam("file") MultipartFile file,
            Principal connectedUser
    ) {
        return service.setProfileImage(file, connectedUser);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAllUsers() {
        return ResponseEntity.ok(service.findAll());
    }

}
