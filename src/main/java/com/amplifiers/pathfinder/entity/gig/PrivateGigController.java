package com.amplifiers.pathfinder.entity.gig;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/gigs")
@RequiredArgsConstructor
public class PrivateGigController {
    private final GigService service;

    @Validated
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createGig(
            @RequestBody GigCreateRequest request
    ) {
        return ResponseEntity.ok(service.createGig(request));
    }
}
