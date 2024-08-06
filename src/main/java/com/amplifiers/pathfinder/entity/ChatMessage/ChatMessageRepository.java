package com.amplifiers.pathfinder.entity.ChatMessage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findAllByChatIdOrderByTimeStampAsc(String chatId);
}
