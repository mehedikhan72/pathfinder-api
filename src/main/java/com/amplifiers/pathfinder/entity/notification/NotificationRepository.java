package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByReceiver(User receiver);
}
