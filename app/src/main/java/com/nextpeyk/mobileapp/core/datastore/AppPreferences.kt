package ir.nextpeyk.android.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import ir.nextpeyk.android.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    @ApplicationScope private val scope: CoroutineScope,
) {

    private object Keys {
        val TOKEN = stringPreferencesKey("auth_token")
    }

    private val _token = MutableStateFlow<String?>(null)
    val tokenFlow: StateFlow<String?> = _token.asStateFlow()

    private val _isLoaded = MutableStateFlow(false)
    val isLoaded: StateFlow<Boolean> = _isLoaded.asStateFlow()

    // Synchronous read — safe for use in OkHttp interceptors without blocking
    val token: String? get() = _token.value

    init {
        scope.launch {
            dataStore.data
                .map { it[Keys.TOKEN] }
                .collect {
                    _token.value = it
                    _isLoaded.value = true
                }
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[Keys.TOKEN] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(Keys.TOKEN) }
    }
}
