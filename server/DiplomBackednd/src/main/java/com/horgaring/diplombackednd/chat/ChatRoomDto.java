package com.horgaring.diplombackednd.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {

    private UUID chatRoomId;
    private UUID matchId;
    private UUID partnerId;
    private String partnerFirstName;
    private String partnerLastName;
    private String partnerAvatarUrl;
    private String lastMessage;
    private Instant lastMessageAt;
    private long unreadCount;
    private Instant createdAt;
}
