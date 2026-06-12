package com.nextpeyk.mobileapp.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nextpeyk.mobileapp.navigation.Screen
import com.nextpeyk.mobileapp.ui.screens.home.components.*
import com.nextpeyk.mobileapp.ui.screens.home.model.HomeTab
import com.nextpeyk.mobileapp.ui.theme.Page

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Page)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            when (uiState.activeTab) {
                HomeTab.Home -> HomeTabView(
                    shipments = viewModel.shipments,
                    openIndex = uiState.openShipmentIndex,
                    showStats = uiState.showStats,
                    onStatsToggle = viewModel::toggleStats,
                    onShipmentSelect = viewModel::setOpenShipment,
                    onDetails = { index ->
                        navController.navigate(Screen.ParcelDetails.createRoute(index))
                    },
                    onProfile = { navController.navigate(Screen.StatsDashboard.route) },
                    modifier = Modifier.fillMaxSize(),
                )
                HomeTab.Map -> MapTabView(modifier = Modifier.fillMaxSize())
                HomeTab.Orders -> OrdersView(
                    shipments = viewModel.shipments,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            FloatingTabBar(
                activeTab = uiState.activeTab,
                onTabSelect = viewModel::setActiveTab,
                onAdd = viewModel::showAddSheet,
                onScan = { navController.navigate(Screen.DeliveryDetail.createRoute(0)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            )

            AnimatedVisibility(visible = uiState.showSearch, enter = fadeIn(), exit = fadeOut()) {
                SearchOverlay(
                    query = uiState.searchQuery,
                    shipments = viewModel.shipments,
                    onQueryChange = viewModel::updateSearchQuery,
                    onDismiss = viewModel::dismissSearch,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            // Stats modal overlay
            AnimatedVisibility(
                visible = uiState.showStats && uiState.activeTab == HomeTab.Home,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Scrim
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.28f))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) { viewModel.dismissStats() },
                    )
                    // Modal card
                    StatsPopover(
                        onClose = viewModel::dismissStats,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 88.dp)
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() },
                            ) {},
                    )
                }
            }
        }

        if (uiState.showAddSheet) {
            AddBottomSheet(onDismiss = viewModel::dismissAddSheet)
        }
    }
}
