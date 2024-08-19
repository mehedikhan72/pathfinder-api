package com.amplifiers.pathfinder.entity.notification;

import com.amplifiers.pathfinder.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notification")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue
    private Integer id;
    private String text;
    private OffsetDateTime timeStamp;

    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    private boolean read;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
    private String linkSuffix; // because we want to click on a notif and be redirected to a specific page.
}
