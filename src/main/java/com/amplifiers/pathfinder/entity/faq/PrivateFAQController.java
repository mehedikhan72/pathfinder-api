package com.amplifiers.pathfinder.entity.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gig/{id}/faq")
@RequiredArgsConstructor
public class PrivateFAQController {
    private final FAQService service;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createFAQ(
            @RequestBody FAQCreateRequest request,
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.createFAQ(request, id));
    }
}
