package com.amplifiers.pathfinder.entity.ChatRoom;

import com.amplifiers.pathfinder.entity.user.User;
import com.amplifiers.pathfinder.entity.user.UserRepository;
import com.amplifiers.pathfinder.exception.ResourceNotFoundException;
import com.amplifiers.pathfinder.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final UserUtility userUtility;

    public Optional<String> getChatRoomId(
            Integer firstUserId,
            Integer secondUserId,
            boolean createNewRoomIfNotExists
    ) {
        // the smaller user id is the first user id.
        if (firstUserId > secondUserId) {
            var temp = firstUserId;
            firstUserId = secondUserId;
            secondUserId = temp;
        }
        // final variables for the lambda functions. (java being weird ik)
        Integer finalFirstUserId = firstUserId;
        Integer finalSecondUserId = secondUserId;

        return chatRoomRepository.findByFirstUserIdAndSecondUserId(finalFirstUserId, finalSecondUserId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(finalFirstUserId, finalSecondUserId);
                        return Optional.of(chatId);
                    }
                    return Optional.empty();
                });
    }

    public String createChatId(Integer firstUserId, Integer secondUserId) {
        var chatId = "";
        if (firstUserId < secondUserId) {
            chatId = String.format("%s_%s", String.valueOf(firstUserId), String.valueOf(secondUserId));
        } else {
            chatId = String.format("%s_%s", String.valueOf(secondUserId), String.valueOf(firstUserId));
        }


        User firstUser = userRepository.findById(firstUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User secondUser = userRepository.findById(secondUserId).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var chatRoom = ChatRoom.builder()
                .chatId(chatId)
                .firstUserId(firstUserId)
                .secondUserId(secondUserId)
                .firstUserFullName(firstUser.getFullName())
                .secondUserFullName(secondUser.getFullName())
                .build();

        chatRoomRepository.save(chatRoom);

        return chatId;
    }

    public List<ChatRoom> getAllContacts(Integer userId) {
        return chatRoomRepository.findAllByFirstUserIdOrSecondUserIdOrderByLastActiveDesc(userId, userId);
    }
}
