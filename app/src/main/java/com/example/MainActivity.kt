package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.auth.attemptAutoSignIn
import com.example.auth.authStateFlow
import com.example.data.repository.FirestoreProxiRepository
import com.example.ui.components.AppTopBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.screens.ProDashboardScreen
import com.example.ui.screens.ProWizardScreen
import com.example.ui.screens.ProfileDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.UnauthenticatedSignInScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreenText
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ProxiAfrikRootGate()
            }
        }
    }
}

@Composable
fun ProxiAfrikRootGate() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val auth = Firebase.auth
    val firebaseUser by auth.authStateFlow()
        .collectAsStateWithLifecycle(initialValue = auth.currentUser)

    LaunchedEffect(Unit) {
        if (auth.currentUser == null) {
            attemptAutoSignIn(
                context = context,
                credentialManager = credentialManager,
                onAuthSuccess = {},
                onUnauthenticated = {},
                scope = scope
            )
        }
    }

    val user = firebaseUser
    if (user == null) {
        UnauthenticatedSignInScreen(
            onAuthSuccess = {}
        )
    } else {
        val uid = user.uid
        key(uid) {
            ProxiAfrikAuthenticatedShell(currentUserId = uid)
        }
    }
}

@Composable
fun ProxiAfrikAuthenticatedShell(currentUserId: String) {
    val context = LocalContext.current
    val viewModel: ProxiAfrikViewModel = viewModel(
        key = currentUserId,
        factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) {
                    "APPLICATION_KEY missing from CreationExtras"
                }
                val databaseId = app.getString(R.string.firestore_database_id)
                val db = FirebaseFirestore.getInstance(databaseId)
                val repo = FirestoreProxiRepository(db, Firebase.auth)
                ProxiAfrikViewModel(repo, currentUserId)
            }
        }
    )

    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    val currentProProfile by viewModel.currentProProfile.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.uiMessages.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

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
                isAdmin = isAdmin,
                onNavigateBack = { viewModel.navigateBack() },
                onNavigateHome = { viewModel.navigateHome() },
                onNavigateToAuth = { viewModel.navigateTo(Screen.Auth) }
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
                NavigationBarItem(
                    selected = currentScreen == Screen.Home,
                    onClick = { viewModel.navigateHome() },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Accueil") },
                    label = { Text("Accueil", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiDarkBlue,
                        selectedTextColor = ProxiDarkBlue,
                        indicatorColor = ProxiDarkBlue.copy(alpha = 0.12f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_home")
                )

                NavigationBarItem(
                    selected = currentScreen is Screen.Search,
                    onClick = { viewModel.navigateTo(Screen.Search()) },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Recherche") },
                    label = { Text("Recherche", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiDarkBlue,
                        selectedTextColor = ProxiDarkBlue,
                        indicatorColor = ProxiDarkBlue.copy(alpha = 0.12f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_search")
                )

                NavigationBarItem(
                    selected = currentScreen == Screen.ProWizard || currentScreen == Screen.ProDashboard,
                    onClick = {
                        if (currentProProfile != null || currentUser?.role == "pro") {
                            viewModel.navigateTo(Screen.ProDashboard)
                        } else {
                            viewModel.navigateTo(Screen.ProWizard)
                        }
                    },
                    icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "Espace Pro") },
                    label = { Text("Espace Pro", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ProxiGreenText,
                        selectedTextColor = ProxiGreenText,
                        indicatorColor = ProxiGreenText.copy(alpha = 0.15f),
                        unselectedIconColor = ProxiTextSecondary,
                        unselectedTextColor = ProxiTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_item_pro")
                )

                // Admin tab ONLY rendered when connected user is a verified Admin in DB
                if (isAdmin) {
                    NavigationBarItem(
                        selected = currentScreen == Screen.AdminDashboard,
                        onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ProxiAmber,
                            selectedTextColor = ProxiAmber,
                            indicatorColor = ProxiAmber.copy(alpha = 0.16f),
                            unselectedIconColor = ProxiTextSecondary,
                            unselectedTextColor = ProxiTextSecondary
                        ),
                        modifier = Modifier.testTag("nav_item_admin")
                    )
                }

                NavigationBarItem(
                    selected = currentScreen == Screen.Auth,
                    onClick = { viewModel.navigateTo(Screen.Auth) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Compte") },
                    label = { Text("Compte", fontSize = 12.sp, fontWeight = FontWeight.Medium) },
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
                is Screen.AdminDashboard -> {
                    if (isAdmin) {
                        AdminDashboardScreen(viewModel = viewModel)
                    } else {
                        HomeScreen(viewModel = viewModel)
                    }
                }
                is Screen.Auth -> AuthScreen(viewModel = viewModel)
                is Screen.Legal -> LegalScreen(
                    type = screen.type,
                    onNavigateBack = { viewModel.navigateBack() }
                )
            }
        }
    }
}
