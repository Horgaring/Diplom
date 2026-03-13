package com.horgaring.dateapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horgaring.dateapp.data.ChatConversation
import com.horgaring.dateapp.data.Message
import com.horgaring.dateapp.data.repository.DateAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = DateAppRepository()

    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<Message>>(emptyList())
    val currentMessages: StateFlow<List<Message>> = _currentMessages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _currentConversation = MutableStateFlow<ChatConversation?>(null)
    val currentConversation: StateFlow<ChatConversation?> = _currentConversation.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _isLoading.value = true
            _conversations.value = repository.getConversations()
            _isLoading.value = false
        }
    }

    fun openConversation(conversationId: String) {
        viewModelScope.launch {
            _currentConversation.value = _conversations.value.find { it.id == conversationId }
            _currentMessages.value = repository.getMessages(conversationId)
        }
    }

    fun sendMessage(conversationId: String, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val message = repository.sendMessage(conversationId, text)
            _currentMessages.value = _currentMessages.value + message

            _conversations.value = _conversations.value.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(
                        lastMessage = text,
                        lastMessageTime = System.currentTimeMillis(),
                        messages = conv.messages + message
                    )
                } else conv
            }
        }
    }
}
