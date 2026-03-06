package com.horgaring.diplombackednd.chat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByChatRoomIdOrderByCreatedAtDesc(UUID chatRoomId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chatRoom.id = :chatRoomId AND m.sender.id <> :userId AND m.read = false")
    long countUnreadInChat(@Param("chatRoomId") UUID chatRoomId, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE Message m SET m.read = true WHERE m.chatRoom.id = :chatRoomId AND m.sender.id <> :userId AND m.read = false")
    void markAllAsRead(@Param("chatRoomId") UUID chatRoomId, @Param("userId") UUID userId);
}
