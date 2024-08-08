package com.amplifiers.pathfinder.entity.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/get")
    public ResponseEntity<?> getNotifications() {
        return ResponseEntity.ok(notificationService.getCurrentUsersNotifications());
    }
}
