package com.amplifiers.pathfinder.entity.ChatMessage;

import com.amplifiers.pathfinder.entity.ChatRoom.ChatRoom;
import com.amplifiers.pathfinder.entity.ChatRoom.ChatRoomRepository;
import com.amplifiers.pathfinder.entity.ChatRoom.ChatRoomService;
import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.exception.ValidationException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserUtility userUtility;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessage save(ChatMessage chatMessage) {
        var chatId = chatRoomService.getChatRoomId(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                true
        ).orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));

        chatMessage.setChatId(chatId);

        User sender = userRepository.findById(chatMessage.getSenderId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User receiver = userRepository.findById(chatMessage.getReceiverId()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(Objects.equals(sender.getId(), receiver.getId())) {
            throw new ValidationException("Cannot send message to self");
        }

        String senderFullName = sender.getFullName();
        String receiverFullName = receiver.getFullName();

        chatMessage.setSenderFullName(senderFullName);
        chatMessage.setReceiverFullName(receiverFullName);

        // so we can fetch contacts sorted by last active.
        ChatRoom chatRoom = chatRoomRepository.findByChatId(chatId).orElseThrow(() -> new ResourceNotFoundException("Chat room not found"));
        chatRoom.setLastActive(OffsetDateTime.now());

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        chatRoom.setLastMessage(savedChatMessage);
        chatRoomRepository.save(chatRoom);

        return savedChatMessage;
    }

    public List<ChatMessage> findChatMessages(Integer firstUserId, Integer secondUserId) {
        if(firstUserId == null || secondUserId == null) {
            throw new ValidationException("User id cannot be null");
        }

        User user = userUtility.getCurrentUser();
        Integer currentUserId = user.getId();
        if (!Objects.equals(currentUserId, firstUserId) && !Objects.equals(currentUserId, secondUserId)) {
            throw new ValidationException("Unauthorized. You can only view your own messages");
        }

        // self messaging ain't doable. as of now.
        if(Objects.equals(firstUserId, secondUserId)) {
            throw new ValidationException("Both user ids must not be the same");
        }

        var chatId = chatRoomService.getChatRoomId(firstUserId, secondUserId, true).orElseThrow();
        readMessages(chatId, currentUserId); // calling it here ensures no further validation is needed.
        return chatMessageRepository.findAllByChatIdOrderByTimeStampAsc(chatId);
    }


    // when a user fetches all the recent messages, it's obvious that the user has read them.
    // so we can mark the messages as read, with receiverId as the current user id, in the chat room.
    public void readMessages(String chatId, Integer currentUserId) {
        chatMessageRepository.findAllByChatIdOrderByTimeStampAsc(chatId)
                .stream()
                .filter(chatMessage -> Objects.equals(chatMessage.getReceiverId(), currentUserId))
                .forEach(chatMessage -> {
                    chatMessage.setRead(true);
                    chatMessageRepository.save(chatMessage);
                });
    }

    public void readSingleMessage(Integer messageId) {
        ChatMessage chatMessage = chatMessageRepository.findById(messageId).orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        User user = userUtility.getCurrentUser();

        // make sure user is supposed to be the receiver of this msg.
        if(!Objects.equals(user.getId(), chatMessage.getReceiverId())) {
            throw new ValidationException("Unauthorized. You can only read your own messages.");
        }

        chatMessage.setRead(true);
        chatMessageRepository.save(chatMessage);
    }

    public boolean userHasUnreadMessages(Integer userId) {
        return chatMessageRepository.userHasUnreadMessages(userId);
    }
}
