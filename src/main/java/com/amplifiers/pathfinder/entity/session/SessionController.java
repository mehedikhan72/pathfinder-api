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
    @PostMapping("/create/{enrollment_id}")
    public ResponseEntity<?> createSession(
            @RequestBody SessionCreateRequest request,
            @PathVariable Integer enrollment_id
    ) {
        return ResponseEntity.ok(service.createSession(request, enrollment_id));
    }

    @PutMapping("/buyer-confirms/{session_id}")
    public ResponseEntity<?> buyerConfirmsSession(
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.buyerConfirmsSession(session_id));
    }

    @PutMapping("/update/{session_id}")
    public ResponseEntity<?> updateSession(
            @RequestBody SessionCreateRequest request,
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.updateSession(request, session_id));
    }

    @PutMapping("/complete/{session_id}")
    public ResponseEntity<?> completeSession(
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.completeSession(session_id));
    }

    @PutMapping("/cancel/{session_id}")
    public ResponseEntity<?> cancelSession(
            @RequestBody SessionCancelRequest request,
            @PathVariable Integer session_id
    ) {
        return ResponseEntity.ok(service.cancelSession(request, session_id));
    }
}
