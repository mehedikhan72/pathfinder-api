package com.amplifiers.pathfinder.entity.gig;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/public/gigs")
@RequiredArgsConstructor
public class PublicGigController {
    private final GigService service;

    @GetMapping("/all")
    public ResponseEntity<?> findAllGigs() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findGigById(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.findById(id));
    }
}
