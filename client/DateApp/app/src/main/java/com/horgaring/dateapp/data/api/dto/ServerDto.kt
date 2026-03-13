package com.horgaring.dateapp.data.api.dto

import com.google.gson.annotations.SerializedName

// ── Auth ─────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String? = null,
    val birthDate: String? = null,
    val gender: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val email: String,
    val firstName: String
)

// ── User ─────────────────────────────────────────────────────

data class UserProfileDto(
    val id: String,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val birthDate: String?,
    val gender: String?,
    val bio: String?,
    val avatarUrl: String?
)

data class UpdateProfileRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val bio: String? = null,
    val gender: String? = null,
    val birthDate: String? = null
)

// ── Dating ───────────────────────────────────────────────────

data class UserCardDto(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val birthDate: String?,
    val gender: String?,
    val bio: String?,
    val city: String?,
    val avatarUrl: String?
)

data class SwipeRequest(
    val targetUserId: String,
    val liked: Boolean
)

data class SwipeResponse(
    val matched: Boolean,
    val matchId: String?,
    val matchedUserId: String?,
    val matchedUserName: String?
)

data class MatchDto(
    val matchId: String,
    val userId: String?,
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val avatarUrl: String?,
    val matchedAt: String?
)

// ── Chat ─────────────────────────────────────────────────────

data class ChatRoomDto(
    val chatRoomId: String,
    val matchId: String?,
    val partnerId: String?,
    val partnerFirstName: String?,
    val partnerLastName: String?,
    val partnerAvatarUrl: String?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Long,
    val createdAt: String?
)

data class MessageDto(
    val id: String,
    val chatRoomId: String?,
    val senderId: String?,
    val senderFirstName: String?,
    val content: String?,
    val read: Boolean,
    val createdAt: String?
)

data class SendMessageRequest(
    val content: String
)

// ── Notifications ────────────────────────────────────────────

data class NotificationDto(
    val id: String,
    val type: String?,
    val title: String?,
    val body: String?,
    val referenceId: String?,
    val read: Boolean,
    val createdAt: String?
)

data class UnreadCountResponse(
    val count: Long
)
