package com.horgaring.diplombackednd.chat;

import com.horgaring.diplombackednd.exception.AccessDeniedException;
import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDto>> getMyChatRooms(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getUserChatRooms(user.getId()));
    }

    @GetMapping("/rooms/{chatRoomId}")
    public ResponseEntity<ChatRoomDto> getChatRoom(
            @AuthenticationPrincipal User user,
            @PathVariable UUID chatRoomId
    ) {
        return ResponseEntity.ok(chatService.getChatRoomDto(chatRoomId, user.getId()));
    }

    @PostMapping("/rooms/match/{matchId}")
    public ResponseEntity<ChatRoomDto> openChat(
            @AuthenticationPrincipal User user,
            @PathVariable UUID matchId
    ) {
        ChatRoom room = chatService.getOrCreateChatRoom(matchId);
        if (!room.getUser1().getId().equals(user.getId())
                && !room.getUser2().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not a participant of this match");
        }
        UUID partnerId = room.getUser1().getId().equals(user.getId())
                ? room.getUser2().getId()
                : room.getUser1().getId();
        String partnerFirst = room.getUser1().getId().equals(user.getId())
                ? room.getUser2().getFirstName()
                : room.getUser1().getFirstName();
        String partnerLast = room.getUser1().getId().equals(user.getId())
                ? room.getUser2().getLastName()
                : room.getUser1().getLastName();
        String partnerAvatar = room.getUser1().getId().equals(user.getId())
                ? room.getUser2().getAvatarUrl()
                : room.getUser1().getAvatarUrl();

        ChatRoomDto dto = ChatRoomDto.builder()
                .chatRoomId(room.getId())
                .matchId(room.getMatch().getId())
                .partnerId(partnerId)
                .partnerFirstName(partnerFirst)
                .partnerLastName(partnerLast)
                .partnerAvatarUrl(partnerAvatar)
                .createdAt(room.getCreatedAt())
                .build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/rooms/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(
            @AuthenticationPrincipal User user,
            @PathVariable UUID chatRoomId
    ) {
        chatService.deleteChatRoom(chatRoomId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rooms/{chatRoomId}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @AuthenticationPrincipal User user,
            @PathVariable UUID chatRoomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(chatService.getMessages(chatRoomId, user.getId(), page, size));
    }

    @MessageMapping("/chat/{chatRoomId}/messages")
    public void processMessage(
            @AuthenticationPrincipal User user,
            @DestinationVariable UUID chatRoomId,
            @Payload SendMessageRequest chatMessage) {

        var message = chatService.sendMessage(chatRoomId, user, chatMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, message);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<MessageDto> getMessage(
            @AuthenticationPrincipal User user,
            @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(chatService.getMessageById(messageId, user.getId()));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User user,
            @PathVariable UUID messageId
    ) {
        chatService.deleteMessage(messageId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rooms/{chatRoomId}/read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable UUID chatRoomId
    ) {
        chatService.markMessagesAsRead(chatRoomId, user.getId());
        return ResponseEntity.ok().build();
    }
}
