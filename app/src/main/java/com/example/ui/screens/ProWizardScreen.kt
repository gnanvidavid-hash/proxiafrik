package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeoCatalog
import com.example.data.model.ServiceItem
import com.example.ui.components.ProxiPhoto
import com.example.ui.components.encodeImageUriToDataUrl
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
import java.util.UUID

@Composable
fun ProWizardScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val draft by viewModel.wizardDraft.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val isSubmitting by viewModel.isSubmittingWizard.collectAsState()

    var newServiceTitle by remember { mutableStateOf("") }
    var newServiceDesc by remember { mutableStateOf("") }
    var newServicePrice by remember { mutableStateOf("") }
    var newServiceIsDevis by remember { mutableStateOf(false) }

    var newPhotoCaption by remember { mutableStateOf("") }
    var isProcessingPhoto by remember { mutableStateOf(false) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var departmentDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

    // Android Zero-Permission Photo Picker for Profile Photo
    val profilePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            isProcessingPhoto = true
            coroutineScope.launch {
                val encoded = encodeImageUriToDataUrl(context, uri, maxDim = 512)
                isProcessingPhoto = false
                if (encoded != null) {
                    viewModel.updateWizardField { d -> d.copy(profileImage = encoded) }
                } else {
                    Toast.makeText(context, "Impossible de charger cette image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Android Zero-Permission Photo Picker for Portfolio Works
    val portfolioPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            isProcessingPhoto = true
            coroutineScope.launch {
                val encoded = encodeImageUriToDataUrl(context, uri, maxDim = 640)
                isProcessingPhoto = false
                if (encoded != null) {
                    val caption = newPhotoCaption.trim().ifBlank { "Réalisation professionnelle" }
                    viewModel.updateWizardField { d ->
                        d.copy(portfolioPhotos = d.portfolioPhotos + Pair(encoded, caption))
                    }
                    newPhotoCaption = ""
                } else {
                    Toast.makeText(context, "Impossible de compresser la photo sélectionnée.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = ProxiTextPrimary,
        unfocusedTextColor = ProxiTextPrimary,
        focusedPlaceholderColor = ProxiTextSecondary,
        unfocusedPlaceholderColor = ProxiTextSecondary,
        focusedBorderColor = ProxiDarkBlue,
        unfocusedBorderColor = ProxiBorder
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("pro_wizard_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // --- STEPPER INDICATOR ---
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
                        text = "Inscription de votre activité",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Étape ${draft.currentStep} sur 6 : ${getStepTitle(draft.currentStep)}",
                        fontSize = 14.sp,
                        color = ProxiGreenText,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        (1..6).forEach { step ->
                            val isCompleted = step < draft.currentStep
                            val isCurrent = step == draft.currentStep
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCompleted -> ProxiGreen
                                            isCurrent -> ProxiDarkBlue
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text(
                                        text = "$step",
                                        color = if (isCurrent) Color.White else ProxiTextSecondary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- STEP CONTENTS ---
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
                    when (draft.currentStep) {
                        1 -> {
                            Text("1. Photo, métier et description", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Real Profile Photo Selection
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ProxiBackground)
                                    .padding(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(ProxiDarkBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ProxiPhoto(
                                        imageUrl = draft.profileImage,
                                        contentDescription = "Photo du professionnel",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        fallbackContent = {
                                            Icon(
                                                imageVector = Icons.Default.PhotoCamera,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Photo de profil / vitrine", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ProxiTextPrimary)
                                    Text(
                                        text = if (draft.profileImage.isBlank()) "Ajoutez votre portrait ou logo d'atelier" else "Photo enregistrée",
                                        fontSize = 12.sp,
                                        color = ProxiTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = {
                                            profilePhotoPicker.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        },
                                        enabled = !isProcessingPhoto,
                                        modifier = Modifier.testTag("wizard_pick_profile_photo_btn")
                                    ) {
                                        Text(
                                            text = if (draft.profileImage.isBlank()) "Choisir une photo" else "Changer la photo",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ProxiDarkBlue
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = draft.businessName,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(businessName = it) } },
                                label = { Text("Nom commercial ou nom d'artisan *") },
                                placeholder = { Text("Ex: Plomberie Moderne Cotonou") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_business_name"),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Box {
                                OutlinedTextField(
                                    value = categories.find { it.id == draft.primaryCategoryId }?.name ?: "Sélectionner une catégorie",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Catégorie principale *") },
                                    modifier = Modifier.fillMaxWidth().clickable { categoryDropdownExpanded = true },
                                    colors = fieldColors
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { categoryDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.name, color = ProxiTextPrimary) },
                                            onClick = {
                                                viewModel.updateWizardField { d -> d.copy(primaryCategoryId = cat.id) }
                                                categoryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.description,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(description = it) } },
                                label = { Text("Description de votre savoir-faire (min. 10 car.) *") },
                                placeholder = { Text("Présentez votre expérience, vos spécialités et vos garanties...") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_description"),
                                minLines = 3,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.ownerName,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(ownerName = it) } },
                                label = { Text("Nom du responsable / artisan") },
                                placeholder = { Text("Ex: M. Jean-Paul Mensah") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = fieldColors
                            )
                        }

                        2 -> {
                            Text("2. Localisation : Pays, Ville et Quartier", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            Box {
                                OutlinedTextField(
                                    value = draft.countryName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pays *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = fieldColors
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { countryDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = countryDropdownExpanded,
                                    onDismissRequest = { countryDropdownExpanded = false }
                                ) {
                                    GeoCatalog.supportedCountries.forEach { country ->
                                        DropdownMenuItem(
                                            text = { Text(country.name, color = ProxiTextPrimary) },
                                            onClick = {
                                                val firstDept = country.departments.firstOrNull()
                                                viewModel.updateWizardField { d ->
                                                    d.copy(
                                                        countryCode = country.code,
                                                        countryName = country.name,
                                                        department = firstDept?.name ?: "",
                                                        city = firstDept?.cities?.firstOrNull() ?: country.allCities.firstOrNull() ?: ""
                                                    )
                                                }
                                                countryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val currentCountryInfo = GeoCatalog.supportedCountries.find { it.code == draft.countryCode }

                            Box {
                                OutlinedTextField(
                                    value = draft.department,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Département / Région *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = fieldColors
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { departmentDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = departmentDropdownExpanded,
                                    onDismissRequest = { departmentDropdownExpanded = false }
                                ) {
                                    currentCountryInfo?.departments?.forEach { dept ->
                                        DropdownMenuItem(
                                            text = { Text(dept.name, color = ProxiTextPrimary) },
                                            onClick = {
                                                viewModel.updateWizardField { d ->
                                                    d.copy(
                                                        department = dept.name,
                                                        city = dept.cities.firstOrNull() ?: ""
                                                    )
                                                }
                                                departmentDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val availableCities = currentCountryInfo?.departments
                                ?.find { it.name.equals(draft.department, ignoreCase = true) }
                                ?.cities ?: currentCountryInfo?.allCities ?: emptyList()
                            Box {
                                OutlinedTextField(
                                    value = draft.city,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Ville *") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = fieldColors
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { cityDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = cityDropdownExpanded,
                                    onDismissRequest = { cityDropdownExpanded = false }
                                ) {
                                    availableCities.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city, color = ProxiTextPrimary) },
                                            onClick = {
                                                viewModel.updateWizardField { d -> d.copy(city = city) }
                                                cityDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.neighborhood,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(neighborhood = it) } },
                                label = { Text("Quartier *") },
                                placeholder = { Text("Ex: Cadjehoun, Godomey, Cocody...") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_neighborhood"),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.interventionZones,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(interventionZones = it) } },
                                label = { Text("Zones d'intervention ou déplacements") },
                                placeholder = { Text("Ex: Cotonou et périphérie, Abomey-Calavi") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = fieldColors
                            )
                        }

                        3 -> {
                            Text("3. Vos prestations et tarifs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Ajoutez les services proposés à vos clients.", fontSize = 13.sp, color = ProxiTextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            if (draft.services.isEmpty()) {
                                Text("Aucun service ajouté pour l'instant.", fontSize = 13.sp, color = ProxiTextSecondary)
                            } else {
                                draft.services.forEachIndexed { index, srv ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(srv.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ProxiTextPrimary)
                                            val priceStr = if (srv.pricingType == "SUR_DEVIS") "Sur devis" else "${srv.indicativePrice.toInt()} FCFA"
                                            Text(priceStr, fontSize = 13.sp, color = ProxiGreenText, fontWeight = FontWeight.Medium)
                                        }
                                        IconButton(onClick = {
                                            viewModel.updateWizardField { d ->
                                                d.copy(services = d.services.filterIndexed { i, _ -> i != index })
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = ProxiRed)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = ProxiBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Ajouter une prestation :", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = ProxiTextPrimary)
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newServiceTitle,
                                onValueChange = { newServiceTitle = it },
                                label = { Text("Intitulé du service (ex: Débouchage express)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newServiceDesc,
                                onValueChange = { newServiceDesc = it },
                                label = { Text("Courte description") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = newServiceIsDevis,
                                    onCheckedChange = { newServiceIsDevis = it }
                                )
                                Text("Prestation sur devis", fontSize = 14.sp, color = ProxiTextPrimary)
                            }

                            if (!newServiceIsDevis) {
                                OutlinedTextField(
                                    value = newServicePrice,
                                    onValueChange = { newServicePrice = it },
                                    label = { Text("Tarif indicatif en FCFA") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    colors = fieldColors
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (newServiceTitle.trim().length >= 2) {
                                        val srv = ServiceItem(
                                            id = "srv_" + UUID.randomUUID().toString().take(8),
                                            professionalId = draft.id,
                                            categoryId = draft.primaryCategoryId,
                                            title = newServiceTitle.trim(),
                                            description = newServiceDesc.trim(),
                                            indicativePrice = newServicePrice.toDoubleOrNull() ?: 0.0,
                                            pricingType = if (newServiceIsDevis) "SUR_DEVIS" else "STARTING_AT"
                                        )
                                        viewModel.updateWizardField { d -> d.copy(services = d.services + srv) }
                                        newServiceTitle = ""
                                        newServiceDesc = ""
                                        newServicePrice = ""
                                        newServiceIsDevis = false
                                    } else {
                                        Toast.makeText(context, "Indiquez l'intitulé du service.", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProxiDarkBlue,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajouter ce service", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

                        4 -> {
                            Text("4. WhatsApp, Téléphone et Consentement", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.phonePublic,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(phonePublic = it) } },
                                label = { Text("Numéro de téléphone public *") },
                                placeholder = { Text("Ex: +229 97 00 00 00") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_phone"),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.whatsappNumber,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(whatsappNumber = it) } },
                                label = { Text("Numéro WhatsApp professionnel *") },
                                placeholder = { Text("Ex: +229 97 00 00 00 (avec indicatif pays)") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_whatsapp"),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.openingHours,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(openingHours = it) } },
                                label = { Text("Horaires déclarés") },
                                placeholder = { Text("Ex: Lun - Sam : 08h00 - 18h30") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Checkbox(
                                    checked = draft.consentGiven,
                                    onCheckedChange = { viewModel.updateWizardField { d -> d.copy(consentGiven = it) } },
                                    modifier = Modifier.testTag("wizard_consent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Je consens expressément à la publication de mon numéro de téléphone et de mon numéro WhatsApp sur mon profil public ProxiAfrik afin de recevoir des demandes de clients.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF78350F),
                                    lineHeight = 18.sp
                                )
                            }
                        }

                        5 -> {
                            Text("5. Photos de vos réalisations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Sélectionnez de vraies photos de vos travaux depuis votre téléphone.", fontSize = 13.sp, color = ProxiTextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            if (draft.portfolioPhotos.isEmpty()) {
                                Text("Aucune photo ajoutée pour l'instant.", fontSize = 13.sp, color = ProxiTextSecondary)
                            } else {
                                draft.portfolioPhotos.forEachIndexed { index, (dataUrl, caption) ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(ProxiBackground)
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ProxiDarkBlue.copy(alpha = 0.1f))
                                        ) {
                                            ProxiPhoto(
                                                imageUrl = dataUrl,
                                                contentDescription = caption,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = caption,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ProxiTextPrimary,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = {
                                            viewModel.updateWizardField { d ->
                                                d.copy(portfolioPhotos = d.portfolioPhotos.filterIndexed { i, _ -> i != index })
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = ProxiRed)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = newPhotoCaption,
                                onValueChange = { newPhotoCaption = it },
                                label = { Text("Légende de la photo (optionnel)") },
                                placeholder = { Text("Ex: Installation salle de bain") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = fieldColors
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    portfolioPhotoPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                enabled = !isProcessingPhoto,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProxiDarkBlue,
                                    contentColor = Color.White
                                ),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isProcessingPhoto) "Traitement de la photo..." else "Choisir une photo dans la galerie",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        6 -> {
                            Text("6. Récapitulatif et soumission", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            if (draft.profileImage.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                ) {
                                    ProxiPhoto(
                                        imageUrl = draft.profileImage,
                                        contentDescription = "Photo du profil",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            Text("Activité : ${draft.businessName.ifBlank { "(non renseigné)" }}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ProxiTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Catégorie : ${categories.find { it.id == draft.primaryCategoryId }?.name ?: ""}", fontSize = 14.sp, color = ProxiTextPrimary)
                            Text("Localisation : ${draft.city} (${draft.neighborhood}) • ${draft.department}, ${draft.countryName}", fontSize = 14.sp, color = ProxiTextSecondary)
                            Text("Téléphone public : ${draft.phonePublic}", fontSize = 14.sp, color = ProxiTextPrimary)
                            Text("WhatsApp : ${draft.whatsappNumber}", fontSize = 14.sp, color = ProxiGreenText, fontWeight = FontWeight.SemiBold)
                            Text("Services : ${draft.services.size} • Photos : ${draft.portfolioPhotos.size}", fontSize = 14.sp, color = ProxiTextSecondary)

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF92400E), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Après soumission, votre profil reste en statut « En attente » et n'apparaîtra pas dans la recherche publique tant qu'un administrateur ne l'aura pas validé.",
                                        fontSize = 13.sp,
                                        color = Color(0xFF78350F),
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (draft.currentStep > 1) {
                            OutlinedButton(
                                onClick = { viewModel.prevWizardStep() },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text("Précédent", color = ProxiDarkBlue, fontWeight = FontWeight.SemiBold)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        if (draft.currentStep < 6) {
                            Button(
                                onClick = {
                                    when (draft.currentStep) {
                                        1 -> {
                                            if (draft.businessName.trim().length < 2) {
                                                Toast.makeText(context, "Veuillez renseigner le nom commercial", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (draft.description.trim().length < 10) {
                                                Toast.makeText(context, "Veuillez rédiger une description d'au moins 10 caractères", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                        }
                                        2 -> {
                                            if (draft.city.isBlank() || draft.neighborhood.trim().isBlank()) {
                                                Toast.makeText(context, "Veuillez indiquer votre ville et votre quartier", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                        }
                                        4 -> {
                                            if (draft.phonePublic.trim().length < 6 || draft.whatsappNumber.trim().length < 6) {
                                                Toast.makeText(context, "Veuillez renseigner des numéros de téléphone et WhatsApp valides", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (!draft.consentGiven) {
                                                Toast.makeText(context, "Vous devez accepter la publication de vos coordonnées", Toast.LENGTH_LONG).show()
                                                return@Button
                                            }
                                        }
                                    }
                                    viewModel.nextWizardStep()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProxiDarkBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp).testTag("wizard_next_button")
                            ) {
                                Text("Suivant", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    viewModel.submitWizard()
                                },
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ProxiGreen,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp).testTag("wizard_submit_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text("Soumettre pour validation", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getStepTitle(step: Int): String {
    return when (step) {
        1 -> "Activité & Photo"
        2 -> "Localisation"
        3 -> "Services"
        4 -> "Contact"
        5 -> "Réalisations"
        else -> "Récapitulatif"
    }
}
