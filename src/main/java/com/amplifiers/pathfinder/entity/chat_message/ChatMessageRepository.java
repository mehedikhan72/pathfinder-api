package com.amplifiers.pathfinder.entity.chat_message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    Page<ChatMessage> findAllByChatIdOrderByTimeStampDesc(Pageable pageable, String chatId);

    @Query("SELECT COUNT(cm) > 0 FROM ChatMessage cm WHERE cm.receiverId = :receiverId AND cm.read = false")
    boolean userHasUnreadMessages(@Param("receiverId") Integer receiverId);
}
