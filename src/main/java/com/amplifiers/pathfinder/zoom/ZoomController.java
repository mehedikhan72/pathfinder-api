package com.amplifiers.pathfinder.zoom;

import com.amplifiers.pathfinder.entity.user.UserService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/zoom")
@RequiredArgsConstructor
public class ZoomController {

    private final UserService userService;

    @GetMapping("/auth-check")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> userAuthCheck(Principal connectedUser) {
        return ResponseEntity.ok(userService.isZoomAuthorized(connectedUser));
    }

    @PostMapping("/auth-code")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> setUserAuthCode(@RequestBody String code, Principal connectedUser) {
        System.out.println(code);
        userService.setAuthorizationCode(code, connectedUser);

        return ResponseEntity.ok(code);
    }
}
