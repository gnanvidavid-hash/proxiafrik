package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.local.ServiceEntity
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiRed
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel

@Composable
fun ProDashboardScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val pro by viewModel.currentProProfile.collectAsState()
    val services by viewModel.currentProServices.collectAsState()
    val portfolio by viewModel.currentProPortfolio.collectAsState()
    val whatsAppClicks by viewModel.currentProWhatsAppClicks.collectAsState()
    val phoneClicks by viewModel.currentProPhoneClicks.collectAsState()
    val subscription by viewModel.currentProSubscription.collectAsState()

    var showAddServiceDialog by remember { mutableStateOf(false) }
    var serviceTitle by remember { mutableStateOf("") }
    var serviceDesc by remember { mutableStateOf("") }
    var servicePrice by remember { mutableStateOf("") }
    var serviceIsDevis by remember { mutableStateOf(false) }

    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var photoCaption by remember { mutableStateOf("") }

    if (pro == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ProxiBackground)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Vous n'avez pas encore créé de fiche d'activité.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = ProxiTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remplissez le formulaire en 6 étapes pour référencer vos prestations sur ProxiAfrik.",
                fontSize = 13.sp,
                color = ProxiTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.ProWizard) },
                colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
            ) {
                Text("Créer mon profil professionnel")
            }
        }
        return
    }

    val currentPro = pro!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("pro_dashboard_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // --- 1. STATUS BANNER ---
        item {
            val (statusBg, statusBorder, statusIcon, statusTitle, statusDesc) = when (currentPro.verificationStatus) {
                "APPROVED" -> StatusInfo(
                    Color(0xFFD1F4DE),
                    ProxiGreen,
                    Icons.Default.CheckCircle,
                    "Profil validé et en ligne",
                    "Votre fiche est visible dans l'annuaire public ProxiAfrik. Les clients peuvent vous contacter directement."
                )
                "REJECTED" -> StatusInfo(
                    Color(0xFFFEE2E2),
                    ProxiRed,
                    Icons.Default.Error,
                    "Profil non validé",
                    "Motif : ${currentPro.rejectionReason ?: "Informations incomplètes ou non conformes."} Veuillez modifier vos informations."
                )
                "SUSPENDED" -> StatusInfo(
                    Color(0xFFFEF3C7),
                    ProxiAmber,
                    Icons.Default.Warning,
                    "Profil suspendu",
                    "Votre profil est temporairement masqué suite à une décision administrative."
                )
                else -> StatusInfo(
                    Color(0xFFFEF3C7),
                    Color(0xFFD97706),
                    Icons.Default.HourglassTop,
                    "En attente de validation",
                    "Votre profil est en cours d'examen par les modérateurs de ProxiAfrik. Vous recevrez une notification dès sa publication."
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusBg)
                    .border(1.dp, statusBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(statusIcon, contentDescription = null, tint = statusBorder, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(statusTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ProxiTextPrimary)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(statusDesc, fontSize = 12.sp, color = ProxiTextSecondary, lineHeight = 16.sp)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 2. STATISTIQUES RÉELLES ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Performances et contacts mesurés",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Text(
                        text = "Statistiques réelles basées sur les clics de clients",
                        fontSize = 12.sp,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Views
                        StatCard(
                            label = "Vues fiche",
                            value = "${currentPro.viewsCount}",
                            icon = Icons.Default.Visibility,
                            iconColor = ProxiDarkBlue,
                            modifier = Modifier.weight(1f)
                        )
                        // WhatsApp
                        StatCard(
                            label = "Clics WhatsApp",
                            value = "$whatsAppClicks",
                            icon = Icons.Default.CheckCircle,
                            iconColor = Color(0xFF25D366),
                            modifier = Modifier.weight(1f)
                        )
                        // Phone calls
                        StatCard(
                            label = "Clics Appel",
                            value = "$phoneClicks",
                            icon = Icons.Default.Call,
                            iconColor = ProxiGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 3. SERVICES DU PROFESSIONNEL ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mes services (${services.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiDarkBlue
                        )
                        TextButton(onClick = { showAddServiceDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter", color = ProxiGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (services.isEmpty()) {
                        Text(
                            text = "Aucun service enregistré. Cliquez sur « Ajouter » ci-dessus.",
                            fontSize = 13.sp,
                            color = ProxiTextSecondary
                        )
                    } else {
                        services.forEachIndexed { index, srv ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(srv.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    val price = if (srv.pricingType == "SUR_DEVIS") "Sur devis" else "${srv.indicativePrice.toInt()} ${srv.currencyCode}"
                                    Text(price, fontSize = 12.sp, color = ProxiGreen, fontWeight = FontWeight.Medium)
                                }
                                IconButton(onClick = { viewModel.deleteService(srv.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                            if (index < services.size - 1) HorizontalDivider(color = ProxiBorder)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 4. GESTION DES PHOTOS DE RÉALISATIONS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Mes photos (${portfolio.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiDarkBlue
                        )
                        TextButton(onClick = { showAddPhotoDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter", color = ProxiGreen, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (portfolio.isEmpty()) {
                        Text(
                            text = "Ajoutez des photos de vos réalisations pour convaincre les clients.",
                            fontSize = 13.sp,
                            color = ProxiTextSecondary
                        )
                    } else {
                        portfolio.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.caption, fontSize = 13.sp, modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.deletePortfolioImage(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 5. ABONNEMENT ACTUEL ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Formule d'abonnement",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val planName = subscription?.planName ?: "Offre Gratuite"
                    Text("Formule active : $planName", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Text(
                        text = "Offre Pro disponible à 2 000 FCFA / mois pour une visibilité accrue et un nombre illimité de photos.",
                        fontSize = 12.sp,
                        color = ProxiTextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.selectProfile(currentPro.id)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aperçu public")
                        }

                        Button(
                            onClick = {
                                Toast.makeText(context, "Demande de contact reçue pour l'Offre Pro.", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Passer à l'offre Pro")
                        }
                    }
                }
            }
        }
    }

    // Add Service Dialog
    if (showAddServiceDialog) {
        AlertDialog(
            onDismissRequest = { showAddServiceDialog = false },
            title = { Text("Ajouter une prestation") },
            text = {
                Column {
                    OutlinedTextField(
                        value = serviceTitle,
                        onValueChange = { serviceTitle = it },
                        label = { Text("Nom du service *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = serviceDesc,
                        onValueChange = { serviceDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = serviceIsDevis, onCheckedChange = { serviceIsDevis = it })
                        Text("Prestation sur devis", fontSize = 13.sp)
                    }
                    if (!serviceIsDevis) {
                        OutlinedTextField(
                            value = servicePrice,
                            onValueChange = { servicePrice = it },
                            label = { Text("Prix indicatif (FCFA)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (serviceTitle.isNotBlank()) {
                            viewModel.addServiceForCurrentPro(
                                serviceTitle,
                                serviceDesc,
                                servicePrice.toDoubleOrNull() ?: 0.0,
                                if (serviceIsDevis) "SUR_DEVIS" else "STARTING_AT"
                            )
                            showAddServiceDialog = false
                            serviceTitle = ""
                            serviceDesc = ""
                            servicePrice = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddServiceDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Photo Dialog
    if (showAddPhotoDialog) {
        AlertDialog(
            onDismissRequest = { showAddPhotoDialog = false },
            title = { Text("Ajouter une photo de travail") },
            text = {
                Column {
                    OutlinedTextField(
                        value = photoCaption,
                        onValueChange = { photoCaption = it },
                        label = { Text("Légende de la photo *") },
                        placeholder = { Text("Ex: Installation compteur électrique") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (photoCaption.isNotBlank()) {
                            viewModel.addPortfolioImageForCurrentPro(photoCaption)
                            showAddPhotoDialog = false
                            photoCaption = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
                ) {
                    Text("Ajouter")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPhotoDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

private data class StatusInfo(
    val bg: Color,
    val border: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val desc: String
)

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ProxiBackground)
            .padding(12.dp)
    ) {
        Column {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ProxiTextPrimary)
            Text(label, fontSize = 11.sp, color = ProxiTextSecondary)
        }
    }
}
