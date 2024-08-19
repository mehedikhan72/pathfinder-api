package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserUtility userUtility;

    public void createNotification(NotificationCreateRequest notificationCreateRequest) {
        Notification notification = Notification.builder()
                .text(notificationCreateRequest.getText())
                .receiver(notificationCreateRequest.getReceiver())
                .type(notificationCreateRequest.getType())
                .linkSuffix(notificationCreateRequest.getLinkSuffix())
                .timeStamp(OffsetDateTime.now())
                .read(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        simpMessagingTemplate.convertAndSendToUser(
                savedNotification.getReceiver().getEmail(),
                "/queue/notifications",
                savedNotification
        );
    }

    public Page<Notification> getCurrentUsersNotifications(Pageable pageable) {
        User receiver = userUtility.getCurrentUser();
        return notificationRepository.findByReceiverOrderByTimeStampDesc(pageable,receiver);
    }

    public boolean userHasUnreadNotifications() {
        User receiver = userUtility.getCurrentUser();
        return notificationRepository.userHasUnreadNotifications(receiver);
    }

    public void markAllUsersNotificationAsRead() {
        User receiver = userUtility.getCurrentUser();
        List<Notification> unreadNotifications = notificationRepository.findByReceiverAndRead(receiver, false);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void sendNotification(String text, User receiver, NotificationType type, String linkSuffix) {
        System.out.println("notification type: " + type);
        NotificationCreateRequest notificationCreateRequest = NotificationCreateRequest.builder()
                .text(text)
                .receiver(receiver)
                .type(type)
                .linkSuffix(linkSuffix)
                .build();

        createNotification(notificationCreateRequest);
    }
}
