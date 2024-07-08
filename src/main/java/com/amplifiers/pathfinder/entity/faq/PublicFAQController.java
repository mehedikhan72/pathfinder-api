package com.amplifiers.pathfinder.entity.faq;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/gig/{id}/faq")
@RequiredArgsConstructor
public class PublicFAQController {
    private final FAQService service;

    @GetMapping("/all")
    public ResponseEntity<?> findAllFAQs(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(service.findAllByGigId(id));
    }
}
