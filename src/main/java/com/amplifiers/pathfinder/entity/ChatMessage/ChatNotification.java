package com.amplifiers.pathfinder.entity.ChatMessage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    @Id
    @GeneratedValue
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String message;
}
