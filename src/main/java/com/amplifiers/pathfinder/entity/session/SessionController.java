package com.amplifiers.pathfinder.entity.session;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/{enrollmentId}")
    public ResponseEntity<?> createSession(
            @RequestBody SessionCreateRequest request,
            @PathVariable Integer enrollmentId
    ) {
        return ResponseEntity.ok(service.createSession(request, enrollmentId));
    }

    @PutMapping("/buyer-confirms/{sessionId}")
    public ResponseEntity<?> buyerConfirmsSession(
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.buyerConfirmsSession(sessionId));
    }

    @PutMapping("/update/{sessionId}")
    public ResponseEntity<?> updateSession(
            @RequestBody SessionCreateRequest request,
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.updateSession(request, sessionId));
    }

    @PutMapping("/complete/{sessionId}")
    public ResponseEntity<?> completeSession(
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.completeSession(sessionId));
    }

    @PutMapping("/cancel/{sessionId}")
    public ResponseEntity<?> cancelSession(
            @RequestBody SessionCancelRequest request,
            @PathVariable Integer sessionId
    ) {
        return ResponseEntity.ok(service.cancelSession(request, sessionId));
    }
}
