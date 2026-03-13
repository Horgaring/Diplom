package com.horgaring.dateapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horgaring.dateapp.data.Match
import com.horgaring.dateapp.data.UserProfile
import com.horgaring.dateapp.data.repository.DateAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SwipeViewModel : ViewModel() {

    private val repository = DateAppRepository()

    private val _profiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val profiles: StateFlow<List<UserProfile>> = _profiles.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _matchResult = MutableStateFlow<Match?>(null)
    val matchResult: StateFlow<Match?> = _matchResult.asStateFlow()

    private val _showMatchAnimation = MutableStateFlow(false)
    val showMatchAnimation: StateFlow<Boolean> = _showMatchAnimation.asStateFlow()

    init {
        loadProfiles()
    }

    fun loadProfiles() {
        viewModelScope.launch {
            _isLoading.value = true
            _profiles.value = repository.getProfiles()
            _currentIndex.value = 0
            _isLoading.value = false
        }
    }

    fun swipeLeft() {
        val profiles = _profiles.value
        val index = _currentIndex.value
        if (index < profiles.size) {
            viewModelScope.launch {
                repository.swipe(profiles[index].id, liked = false)
                _currentIndex.value = index + 1
            }
        }
    }

    fun swipeRight() {
        val profiles = _profiles.value
        val index = _currentIndex.value
        if (index < profiles.size) {
            viewModelScope.launch {
                val match = repository.swipe(profiles[index].id, liked = true)
                if (match != null) {
                    _matchResult.value = match
                    _showMatchAnimation.value = true
                }
                _currentIndex.value = index + 1
            }
        }
    }

    fun dismissMatch() {
        _showMatchAnimation.value = false
        _matchResult.value = null
    }
}
