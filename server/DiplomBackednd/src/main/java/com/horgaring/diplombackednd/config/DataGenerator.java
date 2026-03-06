package com.horgaring.diplombackednd.config;

import com.horgaring.diplombackednd.chat.ChatRoom;
import com.horgaring.diplombackednd.chat.ChatRoomRepository;
import com.horgaring.diplombackednd.chat.Message;
import com.horgaring.diplombackednd.chat.MessageRepository;
import com.horgaring.diplombackednd.dating.LikeRepository;
import com.horgaring.diplombackednd.dating.Match;
import com.horgaring.diplombackednd.dating.MatchRepository;
import com.horgaring.diplombackednd.dating.UserLike;
import com.horgaring.diplombackednd.notification.Notification;
import com.horgaring.diplombackednd.notification.NotificationRepository;
import com.horgaring.diplombackednd.notification.NotificationType;
import com.horgaring.diplombackednd.user.Gender;
import com.horgaring.diplombackednd.user.Role;
import com.horgaring.diplombackednd.user.User;
import com.horgaring.diplombackednd.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataGenerator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final MatchRepository matchRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already contains data, skipping generation.");
            return;
        }

        log.info("Generating seed data...");

        User ivan = userRepository.save(User.builder()
                .email("ivan@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Ivan")
                .lastName("Petrov")
                .birthDate(LocalDate.of(1998, 5, 15))
                .gender(Gender.Male)
                .bio("Looking for someone special!")
                .role(Role.USER)
                .build());

        User anna = userRepository.save(User.builder()
                .email("anna@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Anna")
                .lastName("Sidorova")
                .birthDate(LocalDate.of(2000, 3, 20))
                .gender(Gender.Female)
                .bio("Love hiking and coffee")
                .role(Role.USER)
                .build());

        User maria = userRepository.save(User.builder()
                .email("maria@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Maria")
                .lastName("Ivanova")
                .birthDate(LocalDate.of(1999, 7, 10))
                .gender(Gender.Female)
                .bio("Music lover")
                .role(Role.USER)
                .build());

        User dmitry = userRepository.save(User.builder()
                .email("dmitry@example.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("Dmitry")
                .lastName("Kozlov")
                .birthDate(LocalDate.of(1997, 1, 25))
                .gender(Gender.Male)
                .bio("Sports fan")
                .role(Role.USER)
                .build());

        // Ivan likes Anna
        likeRepository.save(UserLike.builder().liker(ivan).liked(anna).build());
        // Anna likes Ivan -> mutual match
        likeRepository.save(UserLike.builder().liker(anna).liked(ivan).build());

        Match match1 = matchRepository.save(Match.builder()
                .user1(ivan)
                .user2(anna)
                .build());

        // Ivan likes Maria
        likeRepository.save(UserLike.builder().liker(ivan).liked(maria).build());
        // Maria likes Ivan -> mutual match
        likeRepository.save(UserLike.builder().liker(maria).liked(ivan).build());

        Match match2 = matchRepository.save(Match.builder()
                .user1(ivan)
                .user2(maria)
                .build());

        // Dmitry likes Anna (one-sided)
        likeRepository.save(UserLike.builder().liker(dmitry).liked(anna).build());

        // Chat room for Ivan & Anna
        ChatRoom chatRoom1 = chatRoomRepository.save(ChatRoom.builder()
                .match(match1)
                .user1(ivan)
                .user2(anna)
                .build());

        Message msg1 = messageRepository.save(Message.builder()
                .chatRoom(chatRoom1)
                .sender(ivan)
                .content("Hey Anna! Nice to match with you!")
                .build());

        Message msg2 = messageRepository.save(Message.builder()
                .chatRoom(chatRoom1)
                .sender(anna)
                .content("Hi Ivan! Nice to meet you too!")
                .build());

        // Chat room for Ivan & Maria
        ChatRoom chatRoom2 = chatRoomRepository.save(ChatRoom.builder()
                .match(match2)
                .user1(ivan)
                .user2(maria)
                .build());

        messageRepository.save(Message.builder()
                .chatRoom(chatRoom2)
                .sender(ivan)
                .content("Hello Maria!")
                .build());

        // Notifications
        notificationRepository.save(Notification.builder()
                .recipient(ivan)
                .type(NotificationType.NEW_MATCH)
                .title("New match!")
                .body("You matched with Anna!")
                .referenceId(match1.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(anna)
                .type(NotificationType.NEW_MATCH)
                .title("New match!")
                .body("You matched with Ivan!")
                .referenceId(match1.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(ivan)
                .type(NotificationType.NEW_MATCH)
                .title("New match!")
                .body("You matched with Maria!")
                .referenceId(match2.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(ivan)
                .type(NotificationType.NEW_MESSAGE)
                .title("New message from Anna")
                .body("Hi Ivan! Nice to meet you too!")
                .referenceId(chatRoom1.getId())
                .build());

        notificationRepository.save(Notification.builder()
                .recipient(anna)
                .type(NotificationType.NEW_MESSAGE)
                .title("New message from Ivan")
                .body("Hey Anna! Nice to match with you!")
                .referenceId(chatRoom1.getId())
                .build());

        log.info("Seed data generated: 4 users, 2 matches, 2 chat rooms, 3 messages, 5 notifications");
    }
}
