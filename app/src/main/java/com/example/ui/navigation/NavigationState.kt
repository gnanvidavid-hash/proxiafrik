package com.example.ui.navigation

sealed class Screen {
    data object Home : Screen()
    data class Search(
        val categoryId: String = "",
        val countryCode: String = "BJ",
        val department: String = "",
        val city: String = "Cotonou",
        val neighborhood: String = "",
        val query: String = ""
    ) : Screen()
    data class ProfileDetail(val profileId: String) : Screen()
    data object ProWizard : Screen()
    data object ProDashboard : Screen()
    data object AdminDashboard : Screen()
    data object Auth : Screen()
    data class Legal(val type: LegalType) : Screen()
}

enum class LegalType {
    CGU, PRIVACY, PRICING_DISCLAIMER
}
