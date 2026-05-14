package com.horgaring.diplombackednd.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
            ORDER BY
              CASE
                WHEN EXISTS (
                    SELECT 1 FROM UserLike l2
                    WHERE l2.liker.id = u.id AND l2.liked.id = :userId
                ) THEN 0
                ELSE 1
              END,
              function('RANDOM')
            """)
    List<User> findCandidates(@Param("userId") UUID userId, Pageable pageable);

    long countByCreatedAtAfter(Instant since);

    @Query("""
            SELECT u FROM User u
            WHERE (:search IS NULL OR :search = '' OR
                   u.email LIKE CONCAT('%', :search, '%') OR
                   u.firstName LIKE CONCAT('%', :search, '%') OR
                   u.lastName LIKE CONCAT('%', :search, '%'))
              AND (:verified IS NULL OR u.verified = :verified)
            """)
    Page<User> findUsersAdmin(@Param("search") String search,
                              @Param("verified") Boolean verified,
                              Pageable pageable);
}
