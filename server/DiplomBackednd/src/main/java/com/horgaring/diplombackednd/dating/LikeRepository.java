package com.horgaring.diplombackednd.dating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<UserLike, UUID> {

    boolean existsByLikerIdAndLikedId(UUID likerId, UUID likedId);

    Optional<UserLike> findByLikerIdAndLikedId(UUID likerId, UUID likedId);
}
