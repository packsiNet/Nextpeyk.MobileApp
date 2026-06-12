package com.nextpeyk.mobileapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextpeyk.mobileapp.core.datastore.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data object Authenticated : AuthState
}

@HiltViewModel
class AuthViewModel @Inject constructor(prefs: AppPreferences) : ViewModel() {

    val authState: StateFlow<AuthState> = combine(prefs.isLoaded, prefs.tokenFlow) { loaded, token ->
        when {
            !loaded -> AuthState.Loading
            token.isNullOrEmpty() -> AuthState.Unauthenticated
            else -> AuthState.Authenticated
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, AuthState.Loading)
}
