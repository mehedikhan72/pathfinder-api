package com.amplifiers.pathfinder.entity.ChatMessage;

import com.amplifiers.pathfinder.entity.ChatRoom.ChatRoomService;
import com.amplifiers.pathfinder.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                true
        ).orElseThrow();

        chatMessage.setChatId(chatId);
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessages(Integer senderId, Integer receiverId) {
        if(senderId == null || receiverId == null) {
            throw new ValidationException("sender Id and receiver Id must not be null");
        }

        // self messaging ain't doable. as of now.
        if(Objects.equals(senderId, receiverId)) {
            throw new ValidationException("sender Id and receiver Id must not be the same");
        }

        var chatId = chatRoomService.getChatRoomId(senderId, receiverId, true).orElseThrow();
        return chatMessageRepository.findAllByChatId(chatId);
    }
}
