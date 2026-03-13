package com.horgaring.diplombackednd.notification;

import com.horgaring.diplombackednd.exception.AccessDeniedException;
import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public void createNotification(User recipient, NotificationType type, String title, String body, UUID referenceId) {
        log.info("Creating notification: type={}, recipientId={}, referenceId={}", type, recipient.getId(), referenceId);
        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .title(title)
                .body(body)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
        log.debug("Notification saved: id={}", notification.getId());
    }

    public List<NotificationDto> getAllNotifications(UUID userId) {
        log.info("Fetching all notifications for userId={}", userId);
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<NotificationDto> getUnreadNotifications(UUID userId) {
        log.info("Fetching unread notifications for userId={}", userId);
        return notificationRepository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long getUnreadCount(UUID userId) {
        log.debug("Getting unread count for userId={}", userId);
        return notificationRepository.countByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        log.info("Marking all notifications as read for userId={}", userId);
        notificationRepository.markAllAsRead(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        log.info("Marking notification as read: notificationId={}, userId={}", notificationId, userId);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId));
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new AccessDeniedException();
        }
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDto toDto(Notification n) {
        return NotificationDto.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .body(n.getBody())
                .referenceId(n.getReferenceId())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
