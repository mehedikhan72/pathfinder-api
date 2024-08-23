package com.amplifiers.pathfinder.entity.chat_room;

import com.amplifiers.pathfinder.entity.chat_message.ChatMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

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
    @Column(unique = true)
    private String chatId;
    private Integer firstUserId;
    private Integer secondUserId;
    private String firstUserFullName;
    private String secondUserFullName;
    private OffsetDateTime lastActive;

    @OneToOne
    @JoinColumn(name = "lastMessageId")
    private ChatMessage lastMessage;
}
