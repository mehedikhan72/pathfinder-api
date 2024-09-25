package com.amplifiers.pathfinder.entity.session;

import com.amplifiers.pathfinder.entity.user.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService service;
    private final UserService userService;

    private final Integer response450 = 450;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/create/{enrollmentId}")
    public ResponseEntity<?> createSession(@RequestBody SessionCreateRequest request, @PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(service.createSession(request, enrollmentId));
    }

    @PutMapping("/buyer-confirms/{sessionId}")
    public ResponseEntity<?> buyerConfirmsSession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.buyerConfirmsSession(sessionId));
    }

    @PutMapping("/buyer-declines/{sessionId}")
    public ResponseEntity<?> buyerDeclinesSession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.buyerDeclinesSession(sessionId));
    }

    @PutMapping("/update/{sessionId}")
    public ResponseEntity<?> updateSession(@RequestBody SessionCreateRequest request, @PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.updateSession(request, sessionId));
    }

    @PostMapping("/start-zoom/{sessionId}")
    public ResponseEntity<?> startZoomSession(@PathVariable Integer sessionId, Principal connectedUser) {
        if (!userService.isZoomAuthorized(connectedUser)) {
            String redirectUri = CLIENT_BASE_URL + "/zoom-auth";

            System.out.println(redirectUri);

            Dotenv dotenv = Dotenv.load();
            return ResponseEntity.status(response450).body(
                "https://zoom.us/oauth/authorize?response_type=code&client_id=" +
                dotenv.get("ZOOM_CLIENT_ID") +
                "&redirectUri=" +
                redirectUri
            );
        }

        return ResponseEntity.ok(service.startZoomSession(sessionId));
    }

    @GetMapping("/join-zoom/{sessionId}")
    public ResponseEntity<?> joinZoomSession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.joinZoomSession(sessionId));
    }

    @PutMapping("/complete/{sessionId}")
    public ResponseEntity<?> completeSession(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.completeSession(sessionId));
    }

    @PutMapping("/cancel/{sessionId}")
    public ResponseEntity<?> cancelSession(@RequestBody SessionCancelRequest request, @PathVariable Integer sessionId) {
        return ResponseEntity.ok(service.cancelSession(request, sessionId));
    }

    @GetMapping("/running/enrollment/{enrollmentId}")
    public ResponseEntity<?> findRunningSessionByEnrollmentId(@PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(service.findRunningSessionByEnrollmentId(enrollmentId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/all/enrollment/{enrollmentId}")
    public ResponseEntity<?> findAllByEnrollmentId(@PathVariable Integer enrollmentId) {
        return ResponseEntity.ok(service.findAllByEnrollmentId(enrollmentId));
    }
}
