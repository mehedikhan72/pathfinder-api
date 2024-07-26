package com.amplifiers.pathfinder.entity.ChatRoom;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue
    private Integer id;
    private String chatId;
    private Integer senderId;
    private Integer receiverId;
}
