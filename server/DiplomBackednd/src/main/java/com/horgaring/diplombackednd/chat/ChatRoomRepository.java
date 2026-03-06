package com.horgaring.diplombackednd.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {

    Optional<ChatRoom> findByMatchId(UUID matchId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.user1.id = :userId OR cr.user2.id = :userId ORDER BY cr.createdAt DESC")
    List<ChatRoom> findAllByUserId(@Param("userId") UUID userId);
}
