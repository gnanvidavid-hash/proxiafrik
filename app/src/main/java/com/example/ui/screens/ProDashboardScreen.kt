package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProxiPhoto
import com.example.ui.components.encodeImageUriToDataUrl
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiGreenText
import com.example.ui.theme.ProxiRed
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel
import kotlinx.coroutines.launch

@Composable
fun ProDashboardScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val pro by viewModel.currentProProfile.collectAsState()
    val services by viewModel.currentProServices.collectAsState()
    val portfolio by viewModel.currentProPortfolio.collectAsState()

    var showAddServiceDialog by remember { mutableStateOf(false) }
    var serviceTitle by remember { mutableStateOf("") }
    var serviceDesc by remember { mutableStateOf("") }
    var servicePrice by remember { mutableStateOf("") }
    var serviceIsDevis by remember { mutableStateOf(false) }

    var showAddPhotoDialog by remember { mutableStateOf(false) }
    var photoCaption by remember { mutableStateOf("") }
    var pickedPhotoDataUrl by remember { mutableStateOf<String?>(null) }
    var isEncodingPhoto by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            isEncodingPhoto = true
            coroutineScope.launch {
                val encoded = encodeImageUriToDataUrl(context, uri, maxDim = 640)
                isEncodingPhoto = false
                if (encoded != null) {
                    pickedPhotoDataUrl = encoded
                } else {
                    Toast.makeText(context, "Impossible de charger l'image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = ProxiTextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remplissez le formulaire en 6 étapes pour référencer vos prestations sur ProxiAfrik.",
                fontSize = 14.sp,
                color = ProxiTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { viewModel.navigateTo(Screen.ProWizard) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProxiGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier.height(48.dp)
            ) {
                Text("Créer mon profil professionnel", color = Color.White, fontWeight = FontWeight.Bold)
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
                    "Motif : ${currentPro.rejectionReason ?: "Informations incomplètes."} Veuillez vérifier vos données."
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
                    "Votre profil est en cours d'examen par les modérateurs ProxiAfrik avant publication dans la recherche."
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(statusDesc, fontSize = 13.sp, color = ProxiTextSecondary, lineHeight = 18.sp)
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
                        text = "Performances mesurées",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Text(
                        text = "Compteurs enregistrés en base de données",
                        fontSize = 13.sp,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = "Vues fiche",
                            value = "${currentPro.viewsCount}",
                            icon = Icons.Default.Visibility,
                            iconColor = ProxiDarkBlue,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Clics WhatsApp",
                            value = "${currentPro.whatsappClicksCount}",
                            icon = Icons.Default.CheckCircle,
                            iconColor = ProxiGreenText,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Clics Appel",
                            value = "${currentPro.phoneClicksCount}",
                            icon = Icons.Default.Call,
                            iconColor = ProxiGreen,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedButton(
                        onClick = { viewModel.selectProfile(currentPro.id) },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Aperçu de ma fiche", color = ProxiDarkBlue, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 3. SERVICES ---
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
                            Icon(Icons.Default.Add, contentDescription = null, tint = ProxiGreenText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter", color = ProxiGreenText, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (services.isEmpty()) {
                        Text(
                            text = "Aucun service enregistré. Appuyez sur « Ajouter » ci-dessus.",
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
                                    Text(srv.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ProxiTextPrimary)
                                    val price = if (srv.pricingType == "SUR_DEVIS") "Sur devis" else "${srv.indicativePrice.toInt()} ${srv.currencyCode}"
                                    Text(price, fontSize = 13.sp, color = ProxiGreenText, fontWeight = FontWeight.Medium)
                                }
                                IconButton(onClick = { viewModel.deleteService(srv.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = ProxiRed, modifier = Modifier.size(20.dp))
                                }
                            }
                            if (index < services.size - 1) HorizontalDivider(color = ProxiBorder)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 4. GALERIE DE RÉALISATIONS ---
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
                            Icon(Icons.Default.Add, contentDescription = null, tint = ProxiGreenText, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter", color = ProxiGreenText, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (portfolio.isEmpty()) {
                        Text(
                            text = "Ajoutez des photos de vos réalisations pour rassurer les clients.",
                            fontSize = 13.sp,
                            color = ProxiTextSecondary
                        )
                    } else {
                        portfolio.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ProxiBackground)
                                ) {
                                    ProxiPhoto(
                                        imageUrl = item.imageUrl,
                                        contentDescription = item.caption,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(item.caption, fontSize = 13.sp, color = ProxiTextPrimary, modifier = Modifier.weight(1f))
                                IconButton(onClick = { viewModel.deletePortfolioImage(item.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = ProxiRed, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

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
                        Text("Prestation sur devis", fontSize = 14.sp, color = ProxiTextPrimary)
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
                        if (serviceTitle.trim().length >= 2) {
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
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen, contentColor = Color.White)
                ) {
                    Text("Enregistrer", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddServiceDialog = false }) {
                    Text("Annuler", color = ProxiTextSecondary)
                }
            }
        )
    }

    if (showAddPhotoDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddPhotoDialog = false
                pickedPhotoDataUrl = null
                photoCaption = ""
            },
            title = { Text("Ajouter une photo de réalisation") },
            text = {
                Column {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        enabled = !isEncodingPhoto,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = ProxiDarkBlue)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when {
                                isEncodingPhoto -> "Compression..."
                                pickedPhotoDataUrl != null -> "Photo sélectionnée (Changer)"
                                else -> "Choisir une photo *"
                            },
                            color = ProxiDarkBlue
                        )
                    }
                    if (pickedPhotoDataUrl != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            ProxiPhoto(
                                imageUrl = pickedPhotoDataUrl!!,
                                contentDescription = "Aperçu",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
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
                        val url = pickedPhotoDataUrl
                        if (url != null && photoCaption.isNotBlank()) {
                            viewModel.addPortfolioImageForCurrentPro(url, photoCaption.trim())
                            showAddPhotoDialog = false
                            pickedPhotoDataUrl = null
                            photoCaption = ""
                        } else {
                            Toast.makeText(context, "Sélectionnez une photo et ajoutez une légende.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen, contentColor = Color.White)
                ) {
                    Text("Ajouter", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showAddPhotoDialog = false
                    pickedPhotoDataUrl = null
                    photoCaption = ""
                }) {
                    Text("Annuler", color = ProxiTextSecondary)
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
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = ProxiTextSecondary)
        }
    }
}
