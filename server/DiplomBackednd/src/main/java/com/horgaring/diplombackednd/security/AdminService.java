package com.horgaring.diplombackednd.security;

import com.horgaring.diplombackednd.chat.AdminMessageDto;
import com.horgaring.diplombackednd.chat.ChatRoom;
import com.horgaring.diplombackednd.chat.ChatRoomRepository;
import com.horgaring.diplombackednd.chat.Message;
import com.horgaring.diplombackednd.chat.MessageRepository;
import com.horgaring.diplombackednd.dating.MatchRepository;
import com.horgaring.diplombackednd.exception.ResourceNotFoundException;
import com.horgaring.diplombackednd.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MatchRepository matchRepository;
    private final CityRepository cityRepository;

    public Page<User> getUsers(String search, Boolean verified, int page, int size) {
        return userRepository.findUsersAdmin(search, verified, PageRequest.of(page, size));
    }

    public AdminUserDto getUserDetails(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return toAdminDto(user);
    }

    public User updateUserRole(UUID userId, UpdateRoleRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setRole(request.getRole());
        return userRepository.save(user);
    }

    public User toggleUserActive(UUID userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setActive(active);
        return userRepository.save(user);
    }

    public User verifyUser(UUID userId, boolean verified) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        user.setVerified(verified);
        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }

    public Page<Message> getAllMessages(int page, int size) {
        return messageRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    public void deleteMessage(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new ResourceNotFoundException("Message", messageId);
        }
        messageRepository.deleteById(messageId);
    }

    public void deleteChatRoom(UUID chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("ChatRoom", chatRoomId));
        messageRepository.deleteAll(
                messageRepository.findByChatRoomIdOrderByCreatedAtDesc(chatRoomId,
                        PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        chatRoomRepository.delete(chatRoom);
    }

    public List<CityDto> getAllCities() {
        return cityRepository.findAll().stream()
                .map(c -> new CityDto(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    public CityDto createCity(CityDto request) {
        City city = new City();
        city.setName(request.getName());
        City saved = cityRepository.save(city);
        return new CityDto(saved.getId(), saved.getName());
    }

    public CityDto updateCity(UUID cityId, CityDto request) {
        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResourceNotFoundException("City", cityId));
        city.setName(request.getName());
        City saved = cityRepository.save(city);
        return new CityDto(saved.getId(), saved.getName());
    }

    public void deleteCity(UUID cityId) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("City", cityId);
        }
        cityRepository.deleteById(cityId);
    }

    public AdminStatsDto getStats() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getActive())).count();
        long verifiedUsers = userRepository.findAll().stream().filter(u -> Boolean.TRUE.equals(u.getVerified())).count();
        long bannedUsers = totalUsers - activeUsers;
        long totalMatches = matchRepository.count();
        long totalMessages = messageRepository.count();

        Instant now = Instant.now();
        ZoneId zone = ZoneId.of("UTC");
        Instant startOfDay = LocalDate.now(zone).atStartOfDay(zone).toInstant();
        Instant startOfWeek = LocalDate.now(zone).minusDays(LocalDate.now(zone).getDayOfWeek().getValue() - 1).atStartOfDay(zone).toInstant();
        Instant startOfMonth = LocalDate.now(zone).withDayOfMonth(1).atStartOfDay(zone).toInstant();

        long registrationsToday = userRepository.countByCreatedAtAfter(startOfDay);
        long registrationsThisWeek = userRepository.countByCreatedAtAfter(startOfWeek);
        long registrationsThisMonth = userRepository.countByCreatedAtAfter(startOfMonth);

        Map<String, Long> genderDistribution = new LinkedHashMap<>();
        for (Gender g : Gender.values()) {
            long count = userRepository.findAll().stream()
                    .filter(u -> u.getGender() == g).count();
            genderDistribution.put(g.name(), count);
        }

        Map<String, Long> topCities = new LinkedHashMap<>();
        List<City> cities = cityRepository.findAll();
        for (City city : cities) {
            long count = city.getUsers() != null ? city.getUsers().size() : 0;
            if (count > 0) {
                topCities.put(city.getName(), count);
            }
        }

        return AdminStatsDto.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .verifiedUsers(verifiedUsers)
                .bannedUsers(bannedUsers)
                .totalMatches(totalMatches)
                .totalMessages(totalMessages)
                .registrationsToday(registrationsToday)
                .registrationsThisWeek(registrationsThisWeek)
                .registrationsThisMonth(registrationsThisMonth)
                .genderDistribution(genderDistribution)
                .topCities(topCities)
                .build();
    }

    public AdminUserDto toAdminDto(User user) {
        return AdminUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .active(user.getActive())
                .verified(user.getVerified())
                .gender(user.getGender() != null ? user.getGender().name() : null)
                .birthDate(user.getBirthDate())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .cityName(user.getHomeTown() != null ? user.getHomeTown().getName() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }

    public AdminMessageDto toMessageDto(Message message) {
        return AdminMessageDto.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .senderId(message.getSender().getId())
                .senderFirstName(message.getSender().getFirstName())
                .content(message.getContent())
                .read(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
