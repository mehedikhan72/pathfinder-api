package com.amplifiers.pathfinder.entity.ChatMessage;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat")
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    @MessageMapping("/chat")
    public void processMessage(
            @Payload ChatMessage chatMessage
    ) {
        System.out.println("process message called.");
        ChatMessage savedMsg = chatMessageService.save(chatMessage);
        Optional<User> user = userRepository.findById(chatMessage.getReceiverId());
        String receiverEmail = "";
        if(user.isPresent()) {
            receiverEmail = user.get().getEmail();
        }
        System.out.println("sending to user: " + receiverEmail + " with message " + savedMsg);
        messagingTemplate.convertAndSendToUser(
                receiverEmail, "/queue/messages", savedMsg
        );
    }
    @GetMapping("/messages/{firstUserId}/{secondUserId}")
    public ResponseEntity<?> findChatMessages(
            @PathVariable Integer firstUserId,
            @PathVariable Integer secondUserId
    ) {
        return ResponseEntity.ok(chatMessageService.findChatMessages(firstUserId, secondUserId));
    }
}
