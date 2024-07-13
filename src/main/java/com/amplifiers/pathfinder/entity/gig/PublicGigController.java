package com.amplifiers.pathfinder.entity.gig;

import com.amplifiers.pathfinder.entity.video.Video;
import com.amplifiers.pathfinder.entity.video.VideoService;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


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

    @GetMapping("/category/{category}")
    public ResponseEntity<?> findGigsByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(service.findByCategory(category));
    }

    @GetMapping("/search/{query}")
    public ResponseEntity<?> findGigsByQuery(
            @PathVariable String query
    ) {
        return ResponseEntity.ok(service.findByQuery(query));
    }
}
