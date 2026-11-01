package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel

@Composable
fun AuthScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var isRegisterMode by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("CLIENT") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("auth_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (currentUser != null) {
            val user = currentUser!!
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text("Compte connecté", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Nom : ${user.fullName}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Email : ${user.email}", fontSize = 13.sp, color = ProxiTextSecondary)
                        Text("Rôle actuel : ${user.role}", fontSize = 13.sp, color = ProxiGreen, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (user.role == "PROFESSIONAL") {
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.ProDashboard) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Espace Pro")
                                }
                            } else if (user.role == "ADMIN") {
                                Button(
                                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                                    colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tableau Admin")
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.logout()
                                    Toast.makeText(context, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Se déconnecter")
                            }
                        }
                    }
                }
            }
        } else {
            // Login / Register Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Text(
                            text = if (isRegisterMode) "Créer un compte ProxiAfrik" else "Connexion",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiDarkBlue
                        )
                        Text(
                            text = if (isRegisterMode) "Inscrivez-vous pour enregistrer votre activité ou vos favoris" else "Entrez votre email pour accéder à votre espace",
                            fontSize = 12.sp,
                            color = ProxiTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isRegisterMode) {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Nom complet *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Numéro de téléphone *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Adresse email *") },
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (emailInput.isNotBlank()) {
                                    if (isRegisterMode) {
                                        viewModel.register(nameInput.ifBlank { "Utilisateur" }, emailInput, phoneInput, selectedRole)
                                    } else {
                                        viewModel.login(emailInput)
                                    }
                                    Toast.makeText(context, "Bienvenue sur ProxiAfrik !", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_submit_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isRegisterMode) "Créer mon compte" else "Se connecter", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { isRegisterMode = !isRegisterMode },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = if (isRegisterMode) "Vous avez déjà un compte ? Se connecter" else "Pas encore de compte ? S'inscrire",
                                color = ProxiGreen,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- QUICK DEMO ROLES (Tester immédiatement en 1 clic) ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Comptes de test prédéfinis", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ProxiDarkBlue)
                    Text("Basculez instantanément pour tester les 3 rôles de la plateforme :", fontSize = 12.sp, color = ProxiTextSecondary)

                    Spacer(modifier = Modifier.height(12.dp))

                    DemoAccountRow(
                        role = "CLIENT",
                        title = "Compte Client (Visiteur)",
                        desc = "Aïcha Dossou (client@proxiafrik.com)",
                        icon = Icons.Default.Person,
                        iconColor = Color(0xFF64748B),
                        onClick = {
                            viewModel.switchDemoUser("CLIENT")
                            Toast.makeText(context, "Connecté en tant que Client", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DemoAccountRow(
                        role = "PROFESSIONAL",
                        title = "Compte Pro (Artisan)",
                        desc = "Jean-Paul Mensah (pro@proxiafrik.com)",
                        icon = Icons.Default.BusinessCenter,
                        iconColor = ProxiGreen,
                        onClick = {
                            viewModel.switchDemoUser("PROFESSIONAL")
                            Toast.makeText(context, "Connecté en tant que Professionnel", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(Screen.ProDashboard)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DemoAccountRow(
                        role = "ADMIN",
                        title = "Compte Administrateur",
                        desc = "Administrateur ProxiAfrik (admin@proxiafrik.com)",
                        icon = Icons.Default.AdminPanelSettings,
                        iconColor = ProxiAmber,
                        onClick = {
                            viewModel.switchDemoUser("ADMIN")
                            Toast.makeText(context, "Connecté en tant qu'Administrateur", Toast.LENGTH_SHORT).show()
                            viewModel.navigateTo(Screen.AdminDashboard)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoAccountRow(
    role: String,
    title: String,
    desc: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ProxiBackground)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("demo_account_$role"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProxiTextPrimary)
            Text(desc, fontSize = 11.sp, color = ProxiTextSecondary)
        }
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("Activer", fontSize = 11.sp)
        }
    }
}
