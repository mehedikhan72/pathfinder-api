package com.amplifiers.pathfinder.entity.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
    Optional<ChatRoom> findByFirstUserIdAndSecondUserId(Integer firstUserId, Integer secondUserId);
    Optional<ChatRoom> findByChatId(String chatId);
}
