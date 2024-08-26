package com.amplifiers.pathfinder.entity.chat_message;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.amplifiers.pathfinder.utility.Variables;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;
    Integer numMessagesPerPage = Variables.PaginationSettings.NUM_MESSAGES_PER_PAGE;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage) {
        System.out.println("chat message received: " + chatMessage);
        System.out.println("process message called.");
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        Optional<User> user = userRepository.findById(chatMessage.getReceiverId());
        String receiverEmail = "";
        if (user.isPresent()) {
            receiverEmail = user.get().getEmail();
        }
        System.out.println("sending to user: " + receiverEmail + " with message " + savedMsg);
        messagingTemplate.convertAndSendToUser(
                receiverEmail, "/queue/messages", savedMsg);
    }

    @GetMapping("/messages/{firstUserId}/{secondUserId}")
    public ResponseEntity<?> findChatMessages(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @PathVariable Integer firstUserId,
            @PathVariable Integer secondUserId) {
        Pageable pageable = PageRequest.of(page, numMessagesPerPage);
        return ResponseEntity.ok(chatMessageService.findChatMessages(pageable, firstUserId, secondUserId));
    }

    @PutMapping("/messages/read/{messageId}")
    public void readSingleMessage(
            @PathVariable Integer messageId) {
        chatMessageService.readSingleMessage(messageId);
    }

    @GetMapping("/messages/has-unread-messages/{userId}")
    public ResponseEntity<?> userHasUnreadMessages(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(chatMessageService.userHasUnreadMessages(userId));
    }
}
