package com.horgaring.diplombackednd.dating;

import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.notification.NotificationService;
import com.horgaring.diplombackednd.notification.NotificationType;
import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatingService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MatchRepository matchRepository;
    private final NotificationService notificationService;

    public List<UserCardDto> getCandidates(UUID currentUserId) {
        List<User> candidates = userRepository.findCandidates(currentUserId);
        return candidates.stream()
                .map(this::toUserCard)
                .collect(Collectors.toList());
    }

    @Transactional
    public SwipeResponse swipe(UUID currentUserId, SwipeRequest request) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", currentUserId));
        User targetUser = userRepository.findById(request.getTargetUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getTargetUserId()));

        if (!request.getLiked()) {
            return SwipeResponse.builder().matched(false).build();
        }

        if (likeRepository.existsByLikerIdAndLikedId(currentUserId, request.getTargetUserId())) {
            return SwipeResponse.builder().matched(false).build();
        }

        UserLike like = UserLike.builder()
                .liker(currentUser)
                .liked(targetUser)
                .build();
        likeRepository.save(like);

        boolean mutualLike = likeRepository.existsByLikerIdAndLikedId(
                request.getTargetUserId(), currentUserId
        );

        if (mutualLike) {
            Match match = Match.builder()
                    .user1(currentUser)
                    .user2(targetUser)
                    .build();
            matchRepository.save(match);

            notificationService.createNotification(
                    currentUser,
                    NotificationType.NEW_MATCH,
                    "New match!",
                    "You matched with " + targetUser.getFirstName() + "!",
                    match.getId()
            );
            notificationService.createNotification(
                    targetUser,
                    NotificationType.NEW_MATCH,
                    "New match!",
                    "You matched with " + currentUser.getFirstName() + "!",
                    match.getId()
            );

            return SwipeResponse.builder()
                    .matched(true)
                    .matchId(match.getId())
                    .matchedUserId(targetUser.getId())
                    .matchedUserName(targetUser.getFirstName())
                    .build();
        }

        return SwipeResponse.builder().matched(false).build();
    }

    public List<MatchDto> getMatches(UUID currentUserId) {
        List<Match> matches = matchRepository.findAllByUserId(currentUserId);
        return matches.stream()
                .map(match -> {
                    User other = match.getUser1().getId().equals(currentUserId)
                            ? match.getUser2()
                            : match.getUser1();
                    return MatchDto.builder()
                            .matchId(match.getId())
                            .userId(other.getId())
                            .firstName(other.getFirstName())
                            .lastName(other.getLastName())
                            .bio(other.getBio())
                            .avatarUrl(other.getAvatarUrl())
                            .matchedAt(match.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private UserCardDto toUserCard(User user) {
        return UserCardDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .bio(user.getBio())
                .city(user.getHomeTown() != null ? user.getHomeTown().getId().toString() : null)
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
