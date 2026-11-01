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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import com.example.data.local.SeedData
import com.example.data.local.ServiceEntity
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel
import java.util.UUID

@Composable
fun ProWizardScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val draft by viewModel.wizardDraft.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var newServiceTitle by remember { mutableStateOf("") }
    var newServiceDesc by remember { mutableStateOf("") }
    var newServicePrice by remember { mutableStateOf("") }
    var newServiceIsDevis by remember { mutableStateOf(false) }

    var newPhotoCaption by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var countryDropdownExpanded by remember { mutableStateOf(false) }
    var departmentDropdownExpanded by remember { mutableStateOf(false) }
    var cityDropdownExpanded by remember { mutableStateOf(false) }

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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Text(
                        text = "Étape ${draft.currentStep} sur 6 : ${getStepTitle(draft.currentStep)}",
                        fontSize = 13.sp,
                        color = ProxiGreen,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress dots
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
                                    .size(32.dp)
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
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(
                                        text = "$step",
                                        color = if (isCurrent) Color.White else ProxiTextSecondary,
                                        fontSize = 13.sp,
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
                            // ÉTAPE 1 : ACTIVITÉ
                            Text("1. Informations de l'activité", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.businessName,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(businessName = it) } },
                                label = { Text("Nom commercial ou nom d'artisan *") },
                                placeholder = { Text("Ex: Plomberie Moderne Cotonou") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_business_name"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Category selector
                            Box {
                                OutlinedTextField(
                                    value = categories.find { it.id == draft.primaryCategoryId }?.name ?: "Sélectionner une catégorie",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Catégorie principale *") },
                                    modifier = Modifier.fillMaxWidth().clickable { categoryDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = categoryDropdownExpanded,
                                    onDismissRequest = { categoryDropdownExpanded = false }
                                ) {
                                    categories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.name) },
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
                                label = { Text("Description de votre savoir-faire *") },
                                placeholder = { Text("Présentez votre expérience, vos spécialités et vos garanties...") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_description"),
                                minLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.ownerName,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(ownerName = it) } },
                                label = { Text("Nom du responsable / artisan (facultatif)") },
                                placeholder = { Text("Ex: M. Jean-Paul") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                        }

                        2 -> {
                            // ÉTAPE 2 : LOCALISATION
                            Text("2. Localisation et zone d'intervention", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Country
                            Box {
                                OutlinedTextField(
                                    value = draft.countryName,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Pays *") },
                                    modifier = Modifier.fillMaxWidth().clickable { countryDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = countryDropdownExpanded,
                                    onDismissRequest = { countryDropdownExpanded = false }
                                ) {
                                    SeedData.supportedCountries.forEach { country ->
                                        DropdownMenuItem(
                                            text = { Text(country.name) },
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

                            val currentCountryInfo = SeedData.supportedCountries.find { it.code == draft.countryCode }

                            // Department
                            Box {
                                OutlinedTextField(
                                    value = draft.department,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Département / Région *") },
                                    modifier = Modifier.fillMaxWidth().clickable { departmentDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = departmentDropdownExpanded,
                                    onDismissRequest = { departmentDropdownExpanded = false }
                                ) {
                                    currentCountryInfo?.departments?.forEach { dept ->
                                        DropdownMenuItem(
                                            text = { Text(dept.name) },
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

                            // City
                            val availableCities = currentCountryInfo?.departments?.find { it.name.equals(draft.department, ignoreCase = true) }?.cities ?: currentCountryInfo?.allCities ?: emptyList()
                            Box {
                                OutlinedTextField(
                                    value = draft.city,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Ville *") },
                                    modifier = Modifier.fillMaxWidth().clickable { cityDropdownExpanded = true }
                                )
                                DropdownMenu(
                                    expanded = cityDropdownExpanded,
                                    onDismissRequest = { cityDropdownExpanded = false }
                                ) {
                                    availableCities.forEach { city ->
                                        DropdownMenuItem(
                                            text = { Text(city) },
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
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.interventionZones,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(interventionZones = it) } },
                                label = { Text("Zones d'intervention ou déplacements") },
                                placeholder = { Text("Ex: Cotonou et périphérie, Abomey-Calavi") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        3 -> {
                            // ÉTAPE 3 : SERVICES
                            Text("3. Vos prestations et tarifs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ajoutez un ou plusieurs services proposés à vos clients.", fontSize = 12.sp, color = ProxiTextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            // Existing added services
                            if (draft.services.isEmpty()) {
                                Text("Aucun service ajouté. Utilisez le formulaire ci-dessous.", fontSize = 13.sp, color = ProxiTextSecondary)
                            } else {
                                draft.services.forEachIndexed { index, srv ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(srv.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                            val priceStr = if (srv.pricingType == "SUR_DEVIS") "Sur devis" else "${srv.indicativePrice.toInt()} FCFA"
                                            Text(priceStr, fontSize = 12.sp, color = ProxiGreen)
                                        }
                                        IconButton(onClick = {
                                            viewModel.updateWizardField { d ->
                                                d.copy(services = d.services.filterIndexed { i, _ -> i != index })
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = ProxiBorder)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Ajouter une prestation :", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = newServiceTitle,
                                onValueChange = { newServiceTitle = it },
                                label = { Text("Intitulé du service (ex: Débouchage express)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = newServiceDesc,
                                onValueChange = { newServiceDesc = it },
                                label = { Text("Courte description") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = newServiceIsDevis,
                                    onCheckedChange = { newServiceIsDevis = it }
                                )
                                Text("Prestation sur devis", fontSize = 13.sp)
                            }

                            if (!newServiceIsDevis) {
                                OutlinedTextField(
                                    value = newServicePrice,
                                    onValueChange = { newServicePrice = it },
                                    label = { Text("Tarif indicatif en FCFA") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (newServiceTitle.isNotBlank()) {
                                        val srv = ServiceEntity(
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
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ajouter ce service")
                            }
                        }

                        4 -> {
                            // ÉTAPE 4 : CONTACT & CONSENTEMENT
                            Text("4. Coordonnées et horaires de travail", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.phonePublic,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(phonePublic = it) } },
                                label = { Text("Numéro de téléphone public *") },
                                placeholder = { Text("Ex: +229 97 00 00 00") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_phone"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.whatsappNumber,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(whatsappNumber = it) } },
                                label = { Text("Numéro WhatsApp professionnel *") },
                                placeholder = { Text("Ex: +229 97 00 00 00 (avec indicatif pays)") },
                                modifier = Modifier.fillMaxWidth().testTag("wizard_input_whatsapp"),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = draft.openingHours,
                                onValueChange = { viewModel.updateWizardField { d -> d.copy(openingHours = it) } },
                                label = { Text("Horaires déclarés") },
                                placeholder = { Text("Ex: Lun - Sam : 08h00 - 18h30") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mandatory Consent Checkbox
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFEF3C7))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Checkbox(
                                    checked = draft.consentGiven,
                                    onCheckedChange = { viewModel.updateWizardField { d -> d.copy(consentGiven = it) } },
                                    modifier = Modifier.testTag("wizard_consent_checkbox")
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Je consens expressément à la publication de mon numéro de téléphone et de mon numéro WhatsApp sur le profil public ProxiAfrik afin de recevoir des demandes de clients.",
                                    fontSize = 12.sp,
                                    color = Color(0xFF92400E),
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        5 -> {
                            // ÉTAPE 5 : PHOTOS DE RÉALISATIONS
                            Text("5. Photos de vos réalisations", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Les photos permettent aux clients de voir la qualité de vos travaux.", fontSize = 12.sp, color = ProxiTextSecondary)

                            Spacer(modifier = Modifier.height(12.dp))

                            if (draft.portfolioCaptions.isEmpty()) {
                                Text("Aucune photo ajoutée pour l'instant.", fontSize = 13.sp, color = ProxiTextSecondary)
                            } else {
                                draft.portfolioCaptions.forEachIndexed { index, (url, caption) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Photo : $caption", fontSize = 13.sp, modifier = Modifier.weight(1f))
                                        IconButton(onClick = {
                                            viewModel.updateWizardField { d ->
                                                d.copy(portfolioCaptions = d.portfolioCaptions.filterIndexed { i, _ -> i != index })
                                            }
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.Red)
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = newPhotoCaption,
                                onValueChange = { newPhotoCaption = it },
                                label = { Text("Légende de la réalisation (ex: Installation salle de bain)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (newPhotoCaption.isNotBlank()) {
                                        viewModel.updateWizardField { d ->
                                            d.copy(portfolioCaptions = d.portfolioCaptions + Pair("sample_work", newPhotoCaption.trim()))
                                        }
                                        newPhotoCaption = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue)
                            ) {
                                Text("Ajouter cette photo")
                            }
                        }

                        6 -> {
                            // ÉTAPE 6 : RÉCAPITULATIF & SOUMISSION
                            Text("6. Récapitulatif et soumission", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ProxiDarkBlue)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Activité : ${draft.businessName.ifBlank { "(non renseigné)" }}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Catégorie : ${categories.find { it.id == draft.primaryCategoryId }?.name ?: ""}", fontSize = 13.sp)
                            Text("Localisation : ${draft.city} (${draft.neighborhood}) • Dép./Région : ${draft.department}, ${draft.countryName}", fontSize = 13.sp)
                            Text("Téléphone public : ${draft.phonePublic}", fontSize = 13.sp)
                            Text("WhatsApp : ${draft.whatsappNumber}", fontSize = 13.sp)
                            Text("Nombre de services : ${draft.services.size}", fontSize = 13.sp)

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ProxiBackground)
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = ProxiDarkBlue, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Après soumission, votre profil sera mis en statut « En attente ». Un administrateur de ProxiAfrik vérifiera les informations sous 24h avant la mise en ligne.",
                                        fontSize = 12.sp,
                                        color = ProxiTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (draft.currentStep > 1) {
                            OutlinedButton(
                                onClick = { viewModel.prevWizardStep() },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Précédent")
                            }
                        } else {
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        if (draft.currentStep < 6) {
                            Button(
                                onClick = {
                                    // Validation checks
                                    when (draft.currentStep) {
                                        1 -> {
                                            if (draft.businessName.isBlank()) {
                                                Toast.makeText(context, "Veuillez renseigner le nom commercial", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                        }
                                        4 -> {
                                            if (draft.phonePublic.isBlank() || draft.whatsappNumber.isBlank()) {
                                                Toast.makeText(context, "Veuillez renseigner les numéros de contact", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            if (!draft.consentGiven) {
                                                Toast.makeText(context, "Vous devez donner votre consentement pour la publication des coordonnées", Toast.LENGTH_LONG).show()
                                                return@Button
                                            }
                                        }
                                    }
                                    viewModel.nextWizardStep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("wizard_next_button")
                            ) {
                                Text("Suivant")
                            }
                        } else {
                            Button(
                                onClick = {
                                    // Auto login as pro if not logged in
                                    if (currentUser == null) {
                                        viewModel.login("pro@proxiafrik.com", "PROFESSIONAL")
                                    }
                                    viewModel.submitWizard()
                                    Toast.makeText(context, "Profil soumis pour validation avec succès !", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("wizard_submit_button")
                            ) {
                                Text("Soumettre pour validation", fontWeight = FontWeight.Bold)
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
        1 -> "Activité"
        2 -> "Localisation"
        3 -> "Services"
        4 -> "Contact"
        5 -> "Photos"
        else -> "Récapitulatif"
    }
}
