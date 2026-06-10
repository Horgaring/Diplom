package com.horgaring.dateapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.horgaring.dateapp.data.Match
import com.horgaring.dateapp.data.MockData
import com.horgaring.dateapp.data.UserProfile
import com.horgaring.dateapp.data.repository.DateAppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SwipeViewModel : ViewModel() {

    private val repository = DateAppRepository()
    private val pageSize = 10
    private val preloadThreshold = 2

    private var nextPageNumber = 0
    private var hasMorePages = true

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
        refreshProfiles()
    }

    fun loadProfiles() {
        refreshProfiles()
    }

    private fun refreshProfiles() {
        viewModelScope.launch {
            nextPageNumber = 0
            hasMorePages = true
            _profiles.value = emptyList()
            _currentIndex.value = 0
            loadNextPage()
        }
    }

    fun swipeLeft() {
        val profiles = _profiles.value
        val index = _currentIndex.value
        if (index < profiles.size) {
            viewModelScope.launch {
                repository.swipe(profiles[index].id, liked = false)
                val nextIndex = index + 1
                _currentIndex.value = nextIndex
                maybeLoadMore(nextIndex)
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
                val nextIndex = index + 1
                _currentIndex.value = nextIndex
                maybeLoadMore(nextIndex)
            }
        }
    }

    fun dismissMatch() {
        _showMatchAnimation.value = false
        _matchResult.value = null
    }

    private suspend fun maybeLoadMore(nextIndex: Int) {
        val remaining = _profiles.value.size - nextIndex
        if (hasMorePages && remaining <= preloadThreshold) {
            loadNextPage()
        }
    }

    private suspend fun loadNextPage() {
        if (_isLoading.value || !hasMorePages) return

        _isLoading.value = true
        val page = repository.getProfiles(pageSize = pageSize, pageNumber = nextPageNumber)

        if (page.isEmpty() && nextPageNumber == 0) {
            _profiles.value = MockData.profiles
            hasMorePages = false
        } else if (page.isEmpty()) {
            hasMorePages = false
        } else {
            _profiles.value = _profiles.value + page
            nextPageNumber += 1
        }
        _isLoading.value = false
    }
}
