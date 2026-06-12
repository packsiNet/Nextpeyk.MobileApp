package com.nextpeyk.mobileapp.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object ParcelDetails : Screen("parcel_details/{shipmentIndex}") {
        fun createRoute(index: Int) = "parcel_details/$index"
        const val ARG = "shipmentIndex"
    }
    data object DeliveryDetail : Screen("delivery_detail/{shipmentIndex}") {
        fun createRoute(index: Int) = "delivery_detail/$index"
        const val ARG = "shipmentIndex"
    }
    data object DeliveryConfirm : Screen("delivery_confirm/{shipmentIndex}") {
        fun createRoute(index: Int) = "delivery_confirm/$index"
        const val ARG = "shipmentIndex"
    }
    data object ReturnReason : Screen("return_reason/{shipmentIndex}") {
        fun createRoute(index: Int) = "return_reason/$index"
        const val ARG = "shipmentIndex"
    }
    data object StatsDashboard : Screen("stats_dashboard")
}
