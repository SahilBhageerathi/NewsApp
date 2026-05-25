package com.example.newsapp.presentation.viewmodel


import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.domain.model.Contact
import com.example.newsapp.domain.repo.ContactsRepo
import com.example.newsapp.presentation.navigation.AppNavigator
import com.example.newsapp.presentation.navigation.NavigationEffect
import com.example.newsapp.utils.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasPermission: Boolean = false
)

sealed class ContactsEvent {
    data object LoadContacts : ContactsEvent()
    data class PermissionResult(val granted: Boolean) : ContactsEvent()
    data object BackClicked : ContactsEvent()
}

class ContactsViewModel(
    private val repo: ContactsRepo,
    private val dispatchers: DispatcherProvider,
    private val navigator: AppNavigator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    fun onEvent(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.LoadContacts -> loadContacts()
            is ContactsEvent.PermissionResult -> {
                _uiState.update { it.copy(hasPermission = event.granted) }
                if (event.granted) loadContacts()
            }
            is ContactsEvent.BackClicked -> {
                navigator.navigate(NavigationEffect.Back)
            }
        }
    }

    private fun loadContacts() {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val contacts = repo.getContacts()
                withContext(dispatchers.main) {
                    _uiState.update {
                        it.copy(
                            contacts = contacts,
                            isLoading = false
                        )
                    }
                }
            } catch (e: SecurityException) {
                withContext(dispatchers.main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Permission denied"
                        )
                    }
                }
            } catch (e: Exception) {
                withContext(dispatchers.main) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load contacts"
                        )
                    }
                }
            }
        }
    }
}