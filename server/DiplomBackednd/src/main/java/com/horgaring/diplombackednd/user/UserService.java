package com.horgaring.diplombackednd.user;

import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email={}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email={}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
    }

    public List<UserProfileDto> getAllUsers() {
        log.info("Fetching all users");
        List<UserProfileDto> users = userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        log.info("Returned {} users", users.size());
        return users;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }

    public User getUserByEmail(String mail) {
        return userRepository.findByEmail(mail)
                .orElseThrow(() -> new ResourceNotFoundException("User", mail));
    }

    public User activate(String mail) {
        var user = getUserByEmail(mail);
        user.setActive(true);
        userRepository.save(user);
        return user;
    }

    public UserProfileDto getUserProfile(UUID userId) {
        return toDto(getUserById(userId));
    }

    public User updateProfile(UUID userId, UpdateProfileRequest request) {
        log.info("Updating profile for userId={}", userId);
        User user = getUserById(userId);
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getGender() != null) {
            user.setGender(Gender.valueOf(request.getGender()));
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        User saved = userRepository.save(user);
        log.info("Profile updated for userId={}", userId);
        return saved;
    }

    public User updateAvatarUrl(UUID userId, String avatarUrl) {
        log.info("Updating avatar for userId={}", userId);
        User user = getUserById(userId);
        user.setAvatarUrl(avatarUrl);
        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        log.info("Deleting user userId={}", userId);
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
        log.info("User deleted userId={}", userId);
    }

    public UserProfileDto toDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                  .gender(user.getGender() != null ? user.getGender().name() : null)
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
