package com.amplifiers.pathfinder.entity.gig;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/gigs")
@RequiredArgsConstructor
public class PrivateGigController {
    private final GigService service;

    @PostMapping("/create")
    public ResponseEntity<?> createGig(
            @RequestBody GigCreateRequest request
    ) {
        service.save(request);
        return ResponseEntity.accepted().build();
    }
}
