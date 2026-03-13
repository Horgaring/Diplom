package com.horgaring.diplombackednd.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("""
            SELECT u FROM User u
            WHERE u.id <> :userId
              AND u.id NOT IN (
                  SELECT l.liked.id FROM UserLike l WHERE l.liker.id = :userId
              )
            ORDER BY function('RANDOM')
            """)
    List<User> findCandidates(@Param("userId") UUID userId, Pageable pageable);
}
