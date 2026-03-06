package com.horgaring.diplombackednd.notification;

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
public class NotificationDto {

    private UUID id;
    private NotificationType type;
    private String title;
    private String body;
    private UUID referenceId;
    private boolean read;
    private Instant createdAt;
}
