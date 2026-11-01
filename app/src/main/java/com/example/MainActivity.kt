package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.repository.ProxiAfrikRepository
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.ProfileDetailScreen
import com.example.ui.screens.ProDashboardScreen
import com.example.ui.screens.ProWizardScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel
import com.example.ui.viewmodel.ProxiAfrikViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: ProxiAfrikViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProxiAfrikRepository(
            userDao = database.userDao(),
            profileDao = database.professionalProfileDao(),
            categoryDao = database.categoryDao(),
            serviceDao = database.serviceDao(),
            portfolioDao = database.portfolioDao(),
            reviewDao = database.reviewDao(),
            reportDao = database.reportDao(),
            subscriptionDao = database.subscriptionDao(),
            contactEventDao = database.contactEventDao(),
            auditDao = database.adminAuditLogDao()
        )
        ProxiAfrikViewModelFactory(application, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProxiAfrikApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ProxiAfrikApp(viewModel: ProxiAfrikViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Handle Android system back gesture
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                currentUser = currentUser,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateHome = { viewModel.navigateHome() },
                onNavigateToProSpace = {
                    if (currentUser?.role == "PROFESSIONAL") {
                        viewModel.navigateTo(Screen.ProDashboard)
                    } else {
                        viewModel.navigateTo(Screen.ProWizard)
                    }
                },
                onNavigateToAdminSpace = {
                    viewModel.navigateTo(Screen.AdminDashboard)
                },
                onNavigateToAuth = {
                    viewModel.navigateTo(Screen.Auth)
                },
                onSwitchRole = { role ->
                    viewModel.switchDemoUser(role)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                // 1. Home
                NavigationBarItem(
                    selected = currentScreen == Screen.Home,
                    onClick = { viewModel.navigateHome() },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiDarkBlue,
                        selectedTextColor = ProxiDarkBlue,
                        indicatorColor = ProxiDarkBlue.copy(alpha = 0.12f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                // 2. Search
                NavigationBarItem(
                    selected = currentScreen is Screen.Search,
                    onClick = { viewModel.navigateTo(Screen.Search()) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Recherche") },
                    label = { Text("Recherche", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiDarkBlue,
                        selectedTextColor = ProxiDarkBlue,
                        indicatorColor = ProxiDarkBlue.copy(alpha = 0.12f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_search")
                )

                // 3. Pro Space
                NavigationBarItem(
                    selected = currentScreen == Screen.ProWizard || currentScreen == Screen.ProDashboard,
                    onClick = {
                        if (currentUser?.role == "PROFESSIONAL") {
                            viewModel.navigateTo(Screen.ProDashboard)
                        } else {
                            viewModel.navigateTo(Screen.ProWizard)
                        }
                    },
                    icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "Espace Pro") },
                    label = { Text("Espace Pro", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiGreen,
                        selectedTextColor = ProxiGreen,
                        indicatorColor = ProxiGreen.copy(alpha = 0.15f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_pro")
                )

                // 4. Admin Space
                NavigationBarItem(
                    selected = currentScreen == Screen.AdminDashboard,
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFE5A93C),
                        selectedTextColor = Color(0xFFE5A93C),
                        indicatorColor = Color(0xFFE5A93C).copy(alpha = 0.2f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_admin")
                )

                // 5. Account / Auth
                NavigationBarItem(
                    selected = currentScreen == Screen.Auth,
                    onClick = { viewModel.navigateTo(Screen.Auth) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Compte") },
                    label = { Text("Compte", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiDarkBlue,
                        selectedTextColor = ProxiDarkBlue,
                        indicatorColor = ProxiDarkBlue.copy(alpha = 0.12f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_account")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.Search -> SearchScreen(viewModel = viewModel)
                is Screen.ProfileDetail -> ProfileDetailScreen(viewModel = viewModel)
                is Screen.ProWizard -> ProWizardScreen(viewModel = viewModel)
                is Screen.ProDashboard -> ProDashboardScreen(viewModel = viewModel)
                is Screen.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                is Screen.Auth -> AuthScreen(viewModel = viewModel)
                is Screen.Legal -> LegalScreen(type = screen.type)
            }
        }
    }
}
