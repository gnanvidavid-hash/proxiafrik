package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.UserEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen

@Composable
fun AppTopBar(
    currentScreen: Screen,
    currentUser: UserEntity?,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToProSpace: () -> Unit,
    onNavigateToAdminSpace: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onSwitchRole: (String) -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = ProxiDarkBlue,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (currentScreen != Screen.Home) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("topbar_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Retour",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Logo + Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateHome() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_proxiafrik_logo),
                                contentDescription = "Logo ProxiAfrik",
                                modifier = Modifier.size(36.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "ProxiAfrik",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ProxiGreen)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "AFRIQUE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = "Les bons professionnels, près de vous.",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.82f)
                            )
                        }
                    }
                }

                // Role selector & Account toggle
                Box {
                    val roleLabel = when (currentUser?.role) {
                        "ADMIN" -> "Admin"
                        "PROFESSIONAL" -> "Pro"
                        else -> "Visiteur"
                    }
                    val badgeColor = when (currentUser?.role) {
                        "ADMIN" -> Color(0xFFE5A93C)
                        "PROFESSIONAL" -> ProxiGreen
                        else -> Color(0xFF64748B)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { showRoleMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("role_switcher_chip")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(badgeColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = roleLabel,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Changer de rôle",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mode Client / Visiteur") },
                            onClick = {
                                onSwitchRole("CLIENT")
                                showRoleMenu = false
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B))
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mode Professionnel (Pro)") },
                            onClick = {
                                onSwitchRole("PROFESSIONAL")
                                showRoleMenu = false
                                onNavigateToProSpace()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = ProxiGreen)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Mode Administrateur") },
                            onClick = {
                                onSwitchRole("ADMIN")
                                showRoleMenu = false
                                onNavigateToAdminSpace()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFE5A93C))
                            }
                        )
                    }
                }
            }
        }
    }
}
