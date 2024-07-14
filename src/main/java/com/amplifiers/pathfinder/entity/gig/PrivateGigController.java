package com.amplifiers.pathfinder.entity.gig;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/gigs")
@RequiredArgsConstructor
public class PrivateGigController {
    private final GigService service;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createGig(
            @RequestBody GigCreateRequest request
    ) {
        return ResponseEntity.ok(service.createGig(request));
    }

    @PostMapping("/set-cover-image")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setCoverImage(@ModelAttribute GigImageSetRequest gigImageSetRequest) {
        try {
            return ResponseEntity.ok(service.setCoverImage(gigImageSetRequest));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }
    @PostMapping("/set-gig-video")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> setCoverImage(@ModelAttribute GigVideoSetRequest gigVideoSetRequest) {
        try {
            return ResponseEntity.ok(service.setGigVideo(gigVideoSetRequest));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }

    @DeleteMapping("/delete/{gig_id}")
    public String deleteGig(
            @PathVariable Integer gig_id
    ) {
        return service.deleteGig(gig_id);
    }
}
