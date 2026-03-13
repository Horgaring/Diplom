package com.horgaring.dateapp.data.repository

import com.horgaring.dateapp.data.*
import com.horgaring.dateapp.data.api.ApiClient
import com.horgaring.dateapp.data.api.TokenManager
import com.horgaring.dateapp.data.api.dto.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.Period

class DateAppRepository {

    private val api = ApiClient.api

    // ── Auth ───────────────────────────────────────────────────

    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(email, password))
        TokenManager.token = response.token
        TokenManager.userId = response.userId
        TokenManager.email = response.email
        TokenManager.firstName = response.firstName
        return response
    }

    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String?,
        birthDate: String?,
        gender: String?
    ): AuthResponse {
        val response = api.register(
            RegisterRequest(email, password, firstName, lastName, birthDate, gender)
        )
        TokenManager.token = response.token
        TokenManager.userId = response.userId
        TokenManager.email = response.email
        TokenManager.firstName = response.firstName
        return response
    }

    fun logout() {
        TokenManager.clear()
    }

    // ── Users / Profile ─────────────────────────────────────────

    suspend fun getAllUsers(): List<UserProfileDto> {
        return api.getAllUsers()
    }

    suspend fun getUserById(userId: String): UserProfileDto {
        return api.getUserById(userId)
    }

    suspend fun getMyProfile(): UserProfileDto {
        return api.getMyProfile()
    }

    suspend fun updateMyProfile(request: UpdateProfileRequest): UserProfileDto {
        return api.updateMyProfile(request)
    }

    suspend fun deleteUser(userId: String) {
        api.deleteUser(userId)
    }

    suspend fun uploadAvatar(file: File): UserProfileDto {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return api.uploadAvatar(part)
    }

    // ── Dating (candidates → UserProfile for UI) ──────────────

    suspend fun getProfiles(): List<UserProfile> {
        return try {
            api.getCandidates().map { it.toUserProfile() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun swipe(profileId: String, liked: Boolean): Match? {
        return try {
            val response = api.swipe(SwipeRequest(targetUserId = profileId, liked = liked))
            if (response.matched && response.matchId != null) {
                Match(
                    id = response.matchId,
                    user = UserProfile(
                        id = response.matchedUserId ?: "",
                        name = response.matchedUserName ?: "Match"
                    )
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMatches(): List<Match> {
        return try {
            api.getMatches().map { it.toMatch() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Chat ───────────────────────────────────────────────────

    suspend fun getConversations(): List<ChatConversation> {
        return try {
            api.getChatRooms().map { it.toChatConversation() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMessages(chatRoomId: String): List<Message> {
        return try {
            val myId = TokenManager.userId
            api.getMessages(chatRoomId).map { it.toMessage(myId) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun sendMessage(chatRoomId: String, text: String): Message {
        val myId = TokenManager.userId
        return try {
            val dto = api.sendMessage(chatRoomId, SendMessageRequest(content = text))
            dto.toMessage(myId)
        } catch (e: Exception) {
            Message(
                id = "local_${System.currentTimeMillis()}",
                senderId = myId ?: "me",
                receiverId = chatRoomId,
                text = text,
                isFromMe = true
            )
        }
    }

    suspend fun openChat(matchId: String): ChatConversation? {
        return try {
            api.openChat(matchId).toChatConversation()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteChatRoom(chatRoomId: String) {
        try {
            api.deleteChatRoom(chatRoomId)
        } catch (_: Exception) { }
    }

    suspend fun deleteMessage(messageId: String) {
        try {
            api.deleteMessage(messageId)
        } catch (_: Exception) { }
    }

    suspend fun markAsRead(chatRoomId: String) {
        try {
            api.markAsRead(chatRoomId)
        } catch (_: Exception) { }
    }

    // ── Notifications ────────────────────────────────────────────

    suspend fun getNotifications(): List<NotificationDto> {
        return try {
            api.getNotifications()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUnreadNotifications(): List<NotificationDto> {
        return try {
            api.getUnreadNotifications()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUnreadNotificationCount(): Long {
        return try {
            api.getUnreadCount().count
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun markAllNotificationsAsRead() {
        try {
            api.markAllNotificationsAsRead()
        } catch (_: Exception) { }
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        try {
            api.markNotificationAsRead(notificationId)
        } catch (_: Exception) { }
    }

    // ── Mapping helpers ────────────────────────────────────────

    private fun UserCardDto.toUserProfile(): UserProfile {
        val age = birthDate?.let {
            try {
                Period.between(LocalDate.parse(it), LocalDate.now()).years
            } catch (_: Exception) { 0 }
        } ?: 0

        return UserProfile(
            id = id,
            name = listOfNotNull(firstName, lastName).joinToString(" "),
            age = age,
            bio = bio ?: "",
            imageUrl = avatarUrl ?: "",
            location = city ?: "",
            gender = gender ?: ""
        )
    }

    private fun MatchDto.toMatch(): Match {
        return Match(
            id = matchId,
            user = UserProfile(
                id = userId ?: "",
                name = listOfNotNull(firstName, lastName).joinToString(" "),
                bio = bio ?: "",
                imageUrl = avatarUrl ?: ""
            ),
            matchedAt = matchedAt?.let { parseInstantToMillis(it) } ?: System.currentTimeMillis()
        )
    }

    private fun ChatRoomDto.toChatConversation(): ChatConversation {
        val partnerName = listOfNotNull(partnerFirstName, partnerLastName).joinToString(" ")
        return ChatConversation(
            id = chatRoomId,
            match = Match(
                id = matchId ?: "",
                user = UserProfile(
                    id = partnerId ?: "",
                    name = partnerName,
                    imageUrl = partnerAvatarUrl ?: ""
                )
            ),
            lastMessage = lastMessage ?: "",
            lastMessageTime = lastMessageAt?.let { parseInstantToMillis(it) } ?: System.currentTimeMillis(),
            unreadCount = unreadCount.toInt()
        )
    }

    private fun MessageDto.toMessage(myId: String?): Message {
        return Message(
            id = id,
            senderId = senderId ?: "",
            receiverId = "",
            text = content ?: "",
            timestamp = createdAt?.let { parseInstantToMillis(it) } ?: System.currentTimeMillis(),
            isFromMe = senderId == myId
        )
    }

    private fun parseInstantToMillis(value: String): Long {
        return try {
            Instant.parse(value).toEpochMilli()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}
