package ir.nextpeyk.android.ui.screens.home

import androidx.lifecycle.ViewModel
import ir.nextpeyk.android.ui.screens.home.model.HomeTab
import ir.nextpeyk.android.ui.screens.home.model.HomeUiState
import ir.nextpeyk.android.ui.screens.home.model.sampleShipments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    val shipments = sampleShipments

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun setActiveTab(tab: HomeTab) = _uiState.update {
        it.copy(activeTab = tab, showSearch = false)
    }
    fun setOpenShipment(index: Int) = _uiState.update { it.copy(openShipmentIndex = index) }
    fun toggleStats() = _uiState.update { it.copy(showStats = !it.showStats) }
    fun showSearch() = _uiState.update { it.copy(showSearch = true) }
    fun dismissSearch() = _uiState.update { it.copy(showSearch = false, searchQuery = "") }
    fun updateSearchQuery(q: String) = _uiState.update { it.copy(searchQuery = q) }
    fun showScanner() = _uiState.update { it.copy(showScanner = true) }
    fun dismissScanner() = _uiState.update { it.copy(showScanner = false) }
    fun dismissStats() = _uiState.update { it.copy(showStats = false) }
}
