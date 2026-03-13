package com.horgaring.dateapp.data.api

import com.horgaring.dateapp.data.api.dto.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface DateAppApi {

    // ── Auth ─────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    // ── Users ────────────────────────────────────────────────

    @GET("users")
    suspend fun getAllUsers(): List<UserProfileDto>

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): UserProfileDto

    @GET("users/me")
    suspend fun getMyProfile(): UserProfileDto

    @PUT("users/me")
    suspend fun updateMyProfile(@Body request: UpdateProfileRequest): UserProfileDto

    @Multipart
    @POST("users/me/avatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): UserProfileDto

    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String)

    // ── Dating ───────────────────────────────────────────────

    @GET("dating/candidates")
    suspend fun getCandidates(): List<UserCardDto>

    @POST("dating/swipe")
    suspend fun swipe(@Body request: SwipeRequest): SwipeResponse

    @GET("dating/matches")
    suspend fun getMatches(): List<MatchDto>

    // ── Chat ─────────────────────────────────────────────────

    @GET("chat/rooms")
    suspend fun getChatRooms(): List<ChatRoomDto>

    @GET("chat/rooms/{chatRoomId}")
    suspend fun getChatRoom(@Path("chatRoomId") chatRoomId: String): ChatRoomDto

    @POST("chat/rooms/match/{matchId}")
    suspend fun openChat(@Path("matchId") matchId: String): ChatRoomDto

    @DELETE("chat/rooms/{chatRoomId}")
    suspend fun deleteChatRoom(@Path("chatRoomId") chatRoomId: String)

    @GET("chat/rooms/{chatRoomId}/messages")
    suspend fun getMessages(
        @Path("chatRoomId") chatRoomId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): List<MessageDto>

    @POST("chat/rooms/{chatRoomId}/messages")
    suspend fun sendMessage(
        @Path("chatRoomId") chatRoomId: String,
        @Body request: SendMessageRequest
    ): MessageDto

    @GET("chat/messages/{messageId}")
    suspend fun getMessageById(@Path("messageId") messageId: String): MessageDto

    @DELETE("chat/messages/{messageId}")
    suspend fun deleteMessage(@Path("messageId") messageId: String)

    @PUT("chat/rooms/{chatRoomId}/read")
    suspend fun markAsRead(@Path("chatRoomId") chatRoomId: String)

    // ── Notifications ────────────────────────────────────────

    @GET("notifications")
    suspend fun getNotifications(): List<NotificationDto>

    @GET("notifications/unread")
    suspend fun getUnreadNotifications(): List<NotificationDto>

    @GET("notifications/unread/count")
    suspend fun getUnreadCount(): UnreadCountResponse

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsAsRead()

    @PUT("notifications/{notificationId}/read")
    suspend fun markNotificationAsRead(@Path("notificationId") notificationId: String)
}
