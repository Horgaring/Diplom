package com.horgaring.diplombackednd.security;

import com.horgaring.diplombackednd.chat.AdminMessageDto;
import com.horgaring.diplombackednd.chat.Message;
import com.horgaring.diplombackednd.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDto>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<User> users = adminService.getUsers(search, verified, page, size);
        Page<AdminUserDto> dtoPage = users.map(adminService::toAdminDto);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDto> getUserDetails(@PathVariable UUID userId) {
        return ResponseEntity.ok(adminService.getUserDetails(userId));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<AdminUserDto> updateUserRole(
            @PathVariable UUID userId,
            @RequestBody UpdateRoleRequest request) {
        User user = adminService.updateUserRole(userId, request);
        return ResponseEntity.ok(adminService.toAdminDto(user));
    }

    @PutMapping("/users/{userId}/active")
    public ResponseEntity<AdminUserDto> toggleUserActive(
            @PathVariable UUID userId,
            @RequestBody ActiveRequest request) {
        User user = adminService.toggleUserActive(userId, request.isActive());
        return ResponseEntity.ok(adminService.toAdminDto(user));
    }

    @PutMapping("/users/{userId}/verify")
    public ResponseEntity<AdminUserDto> verifyUser(
            @PathVariable UUID userId,
            @RequestBody VerifyRequest request) {
        User user = adminService.verifyUser(userId, request.isVerified());
        return ResponseEntity.ok(adminService.toAdminDto(user));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages")
    public ResponseEntity<Page<AdminMessageDto>> getAllMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Message> messages = adminService.getAllMessages(page, size);
        return ResponseEntity.ok(messages.map(adminService::toMessageDto));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable UUID messageId) {
        adminService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/chat/rooms/{chatRoomId}")
    public ResponseEntity<Void> deleteChatRoom(@PathVariable UUID chatRoomId) {
        adminService.deleteChatRoom(chatRoomId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cities")
    public ResponseEntity<List<CityDto>> getAllCities() {
        return ResponseEntity.ok(adminService.getAllCities());
    }

    @PostMapping("/cities")
    public ResponseEntity<CityDto> createCity(@RequestBody CityDto request) {
        return ResponseEntity.ok(adminService.createCity(request));
    }

    @PutMapping("/cities/{cityId}")
    public ResponseEntity<CityDto> updateCity(
            @PathVariable UUID cityId,
            @RequestBody CityDto request) {
        return ResponseEntity.ok(adminService.updateCity(cityId, request));
    }

    @DeleteMapping("/cities/{cityId}")
    public ResponseEntity<Void> deleteCity(@PathVariable UUID cityId) {
        adminService.deleteCity(cityId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}
