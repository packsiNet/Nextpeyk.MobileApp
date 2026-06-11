package com.nextpeyk.mobileapp.ui.screens.home.model

enum class HomeTab { Home, Map, Orders }

data class HomeUiState(
    val activeTab: HomeTab = HomeTab.Home,
    val openShipmentIndex: Int = 0,
    val showSearch: Boolean = false,
    val showAddSheet: Boolean = false,
    val showScanner: Boolean = false,
    val showStats: Boolean = false,
    val searchQuery: String = "",
)
