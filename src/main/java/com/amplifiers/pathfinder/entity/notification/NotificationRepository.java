package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiverOrderByTimeStampDesc(User receiver);

    @Query("SELECT COUNT(n) > 0 FROM Notification n WHERE n.receiver = :receiver AND n.read = false")
    boolean userHasUnreadNotifications(@Param("receiver") User receiver);

    // returns a user's unread notifications.
    List<Notification> findByReceiverAndRead(User receiver, boolean read);
}
