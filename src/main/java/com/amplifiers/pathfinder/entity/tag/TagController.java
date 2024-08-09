package com.amplifiers.pathfinder.entity.tag;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService service;

    @GetMapping("/all")
    public ResponseEntity<?> findAllTags() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping({"/search/", "/search/{query}"})
    public ResponseEntity<?> findTagsByQuery(
            @PathVariable(required = false) String query
    ) {
        return ResponseEntity.ok(service.findByQuery(query));
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createTag(
            @RequestBody TagCreateRequest request
    ) {
        return ResponseEntity.ok(service.createTag(request));
    }

}
