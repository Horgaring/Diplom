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
public class AdminMessageDto {
    private UUID id;
    private UUID chatRoomId;
    private UUID senderId;
    private String senderFirstName;
    private String content;
    private boolean read;
    private Instant createdAt;
}
