package com.amplifiers.pathfinder.entity.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findAllByChatIdOrderByTimeStampAsc(String chatId);

    @Query("SELECT COUNT(cm) > 0 FROM ChatMessage cm WHERE cm.receiverId = :receiverId AND cm.read = false")
    boolean userHasUnreadMessages(@Param("receiverId") Integer receiverId);
}
