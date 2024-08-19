package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.entity.user.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    public String text;
    public User receiver;

    @Enumerated(EnumType.STRING)
    public NotificationType type;
    public String linkSuffix;
}
