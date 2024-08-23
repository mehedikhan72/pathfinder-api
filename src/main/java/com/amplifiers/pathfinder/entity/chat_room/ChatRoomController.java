package com.amplifiers.pathfinder.entity.chat_room;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/chat-room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllContacts (
            @PathVariable Integer userId
    ) {
        return ResponseEntity.ok(chatRoomService.getAllContacts(userId));
    }
}
