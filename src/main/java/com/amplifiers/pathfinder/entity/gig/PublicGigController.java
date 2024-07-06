package com.amplifiers.pathfinder.entity.gig;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.GsonJsonParser;
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

    @PostMapping("/set-cover-image")
    public ResponseEntity<?> setCoverImage(@ModelAttribute GigImageSetRequest gigImageSetRequest) {
        try {
            return ResponseEntity.ok(service.setCoverImage(gigImageSetRequest));
        } catch (Exception E) {
            E.printStackTrace();
            return ResponseEntity.status(400).body(E.getMessage());
        }
    }
}
