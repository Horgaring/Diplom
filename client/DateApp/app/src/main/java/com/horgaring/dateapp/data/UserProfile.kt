package com.horgaring.dateapp.data

data class UserProfile(
    val id: String,
    val name: String = "",
    val age: Int = 0,
    val bio: String = "",
    val imageUrl: String? = "",
    val interests: List<String> = emptyList(),
    val location: String = "",
    val gender: String = "",
    val lookingFor: String = ""
)

data class Match(
    val id: String,
    val user: UserProfile,
    val matchedAt: Long = System.currentTimeMillis()
)

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false
)

data class ChatConversation(
    val id: String,
    val match: Match,
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val messages: List<Message> = emptyList()
)

