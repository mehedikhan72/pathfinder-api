package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.utility.Variables;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    Integer numNotificationsPerPage = Variables.PaginationSettings.NUM_NOTIFICATIONS_PER_PAGE;

    @GetMapping("/get")
    public ResponseEntity<?> getNotifications(
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, numNotificationsPerPage);
        return ResponseEntity.ok(notificationService.getCurrentUsersNotifications(pageable));
    }

    @GetMapping("/user-has-unread-notifications")
    public ResponseEntity<?> userHasUnreadNotifications() {
        return ResponseEntity.ok(notificationService.userHasUnreadNotifications());
    }

    @PutMapping("/mark-all-as-read")
    public void markAllAsRead() {
        notificationService.markAllUsersNotificationAsRead();
    }
}
