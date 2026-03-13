package com.horgaring.diplombackednd.chat;

import com.horgaring.diplombackednd.dating.Match;
import com.horgaring.diplombackednd.dating.MatchRepository;
import com.horgaring.diplombackednd.exception.AccessDeniedException;
import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.notification.NotificationService;
import com.horgaring.diplombackednd.notification.NotificationType;
import com.horgaring.diplombackednd.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    public ChatRoom getOrCreateChatRoom(UUID matchId) {
        log.info("Getting or creating chat room for matchId={}", matchId);
        return chatRoomRepository.findByMatchId(matchId)
                .orElseGet(() -> {
                    log.info("Creating new chat room for matchId={}", matchId);
                    Match match = matchRepository.findById(matchId)
                            .orElseThrow(() -> new ResourceNotFoundException("Match", matchId));
                    ChatRoom chatRoom = ChatRoom.builder()
                            .match(match)
                            .user1(match.getUser1())
                            .user2(match.getUser2())
                            .build();
                    ChatRoom saved = chatRoomRepository.save(chatRoom);
                    log.info("Chat room created: chatRoomId={}", saved.getId());
                    return saved;
                });
    }

    public List<ChatRoomDto> getUserChatRooms(UUID userId) {
        log.info("Getting chat rooms for userId={}", userId);
        List<ChatRoom> rooms = chatRoomRepository.findAllByUserId(userId);
        log.info("Found {} chat rooms for userId={}", rooms.size(), userId);
        return rooms.stream()
                .map(room -> toChatRoomDto(room, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public MessageDto sendMessage(UUID chatRoomId, User sender, SendMessageRequest request) {
        log.info("Sending message in chatRoomId={} from userId={}", chatRoomId, sender.getId());
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));

        if (!chatRoom.getUser1().getId().equals(sender.getId())
                && !chatRoom.getUser2().getId().equals(sender.getId())) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .build();
        messageRepository.save(message);
        log.info("Message saved: messageId={} in chatRoomId={}", message.getId(), chatRoomId);

        User recipient = chatRoom.getUser1().getId().equals(sender.getId())
                ? chatRoom.getUser2()
                : chatRoom.getUser1();

        notificationService.createNotification(
                recipient,
                NotificationType.NEW_MESSAGE,
                "New message from " + sender.getFirstName(),
                request.getContent().length() > 100
                        ? request.getContent().substring(0, 100) + "..."
                        : request.getContent(),
                chatRoomId
        );

        return toMessageDto(message);
    }

    public List<MessageDto> getMessages(UUID chatRoomId, UUID userId, int page, int size) {
        log.debug("Getting messages for chatRoomId={}, userId={}, page={}, size={}", chatRoomId, userId, page, size);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));

        if (!chatRoom.getUser1().getId().equals(userId)
                && !chatRoom.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        Page<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtDesc(
                chatRoomId, PageRequest.of(page, size)
        );

        return messages.getContent().stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(UUID chatRoomId, UUID userId) {
        log.info("Marking messages as read in chatRoomId={} for userId={}", chatRoomId, userId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));

        if (!chatRoom.getUser1().getId().equals(userId)
                && !chatRoom.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        messageRepository.markAllAsRead(chatRoomId, userId);
        log.info("Messages marked as read in chatRoomId={} for userId={}", chatRoomId, userId);
    }

    public ChatRoomDto getChatRoomDto(UUID chatRoomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));

        if (!chatRoom.getUser1().getId().equals(userId)
                && !chatRoom.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        return toChatRoomDto(chatRoom, userId);
    }

    @Transactional
    public void deleteChatRoom(UUID chatRoomId, UUID userId) {
        log.info("Deleting chat room chatRoomId={} by userId={}", chatRoomId, userId);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));

        if (!chatRoom.getUser1().getId().equals(userId)
                && !chatRoom.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        messageRepository.deleteAll(messageRepository.findByChatRoomIdOrderByCreatedAtDesc(
                chatRoomId, PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent());
        chatRoomRepository.delete(chatRoom);
        log.info("Chat room deleted: chatRoomId={}", chatRoomId);
    }

    public MessageDto getMessageById(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        ChatRoom chatRoom = message.getChatRoom();
        if (!chatRoom.getUser1().getId().equals(userId)
                && !chatRoom.getUser2().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant of this chat");
        }

        return toMessageDto(message);
    }

    @Transactional
    public void deleteMessage(UUID messageId, UUID userId) {
        log.info("Deleting message messageId={} by userId={}", messageId, userId);
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", messageId));

        if (!message.getSender().getId().equals(userId)) {
            throw new AccessDeniedException("You can only delete your own messages");
        }

        messageRepository.delete(message);
        log.info("Message deleted: messageId={}", messageId);
    }

    private ChatRoomDto toChatRoomDto(ChatRoom room, UUID currentUserId) {
        User partner = room.getUser1().getId().equals(currentUserId)
                ? room.getUser2()
                : room.getUser1();

        Page<Message> lastMessages = messageRepository.findByChatRoomIdOrderByCreatedAtDesc(
                room.getId(), PageRequest.of(0, 1)
        );

        long unread = messageRepository.countUnreadInChat(room.getId(), currentUserId);

        String lastMsg = null;
        java.time.Instant lastMsgAt = null;
        if (!lastMessages.isEmpty()) {
            Message last = lastMessages.getContent().get(0);
            lastMsg = last.getContent();
            lastMsgAt = last.getCreatedAt();
        }

        return ChatRoomDto.builder()
                .chatRoomId(room.getId())
                .matchId(room.getMatch().getId())
                .partnerId(partner.getId())
                .partnerFirstName(partner.getFirstName())
                .partnerLastName(partner.getLastName())
                .partnerAvatarUrl(partner.getAvatarUrl())
                .lastMessage(lastMsg)
                .lastMessageAt(lastMsgAt)
                .unreadCount(unread)
                .createdAt(room.getCreatedAt())
                .build();
    }

    private MessageDto toMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderFirstName(message.getSender().getFirstName())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
