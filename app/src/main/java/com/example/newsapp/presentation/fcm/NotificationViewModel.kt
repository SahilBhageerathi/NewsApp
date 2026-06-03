package com.example.newsapp.presentation.fcm

import androidx.lifecycle.ViewModel
import com.example.newsapp.core.fcmMessaging.LocalNotifier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NotificationViewModel(
    private val notifier: LocalNotifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    fun sendNotification() {
        val count = _uiState.value.notificationCount + 1
        val title = "Test Notification #$count"
        val body = "This is a locally-triggered notification."

        notifier.showNotification(title, body)

        _uiState.update {
            it.copy(
                notificationCount = count,
                lastMessage = body
            )
        }
    }
}

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notificationCount: Int = 0,
    val lastMessage: String? = null
)