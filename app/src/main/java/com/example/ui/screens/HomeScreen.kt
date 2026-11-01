package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.data.local.SeedData
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.ProfileCard
import com.example.ui.navigation.LegalType
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiDarkBlueVariant
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel

@Composable
fun HomeScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val selectedDepartment by viewModel.selectedDepartment.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val approvedProfiles by viewModel.approvedProfiles.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var countryMenuExpanded by remember { mutableStateOf(false) }
    var departmentMenuExpanded by remember { mutableStateOf(false) }
    var cityMenuExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // --- 1. HERO SECTION ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(ProxiDarkBlue, ProxiDarkBlueVariant)
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // African Market Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ProxiGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Réseau de prestataires en Afrique",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Les bons professionnels,\nprès de vous.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 32.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Trouvez rapidement un artisan fiable, un technicien ou un prestataire dans votre quartier et contactez-le directement.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Location pickers (Country, Department, City)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Country Selector
                            Box(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .clickable { countryMenuExpanded = true }
                                        .padding(horizontal = 10.dp, vertical = 9.dp)
                                        .testTag("home_country_selector"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = ProxiGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text("Pays", fontSize = 9.sp, color = ProxiTextSecondary)
                                        Text(
                                            selectedCountry.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ProxiTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = countryMenuExpanded,
                                    onDismissRequest = { countryMenuExpanded = false }
                                ) {
                                    viewModel.countries.forEach { country ->
                                        DropdownMenuItem(
                                            text = { Text("${country.name} (${country.code})") },
                                            onClick = {
                                                viewModel.setCountry(country)
                                                countryMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Department Selector
                            Box(modifier = Modifier.weight(1.2f)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .clickable { departmentMenuExpanded = true }
                                        .padding(horizontal = 10.dp, vertical = 9.dp)
                                        .testTag("home_dept_selector"),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Département / Région", fontSize = 9.sp, color = ProxiTextSecondary)
                                        Text(
                                            selectedDepartment.ifBlank { "Tous" },
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ProxiDarkBlue,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                DropdownMenu(
                                    expanded = departmentMenuExpanded,
                                    onDismissRequest = { departmentMenuExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Tous les départements") },
                                        onClick = {
                                            viewModel.setDepartment("")
                                            departmentMenuExpanded = false
                                        }
                                    )
                                    selectedCountry.departments.forEach { dept ->
                                        DropdownMenuItem(
                                            text = { Text(dept.name) },
                                            onClick = {
                                                viewModel.setDepartment(dept.name)
                                                departmentMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // City Selector
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White)
                                    .clickable { cityMenuExpanded = true }
                                    .padding(horizontal = 10.dp, vertical = 9.dp)
                                    .testTag("home_city_selector"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = ProxiDarkBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text("Ville sélectionnée", fontSize = 9.sp, color = ProxiTextSecondary)
                                    Text(
                                        selectedCity.ifBlank { "Toutes les villes" },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProxiTextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            val availableCities = selectedCountry.departments.find { it.name.equals(selectedDepartment, ignoreCase = true) }?.cities ?: selectedCountry.allCities

                            DropdownMenu(
                                expanded = cityMenuExpanded,
                                onDismissRequest = { cityMenuExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Toutes les villes") },
                                    onClick = {
                                        viewModel.setCity("")
                                        cityMenuExpanded = false
                                    }
                                )
                                availableCities.forEach { city ->
                                    DropdownMenuItem(
                                        text = { Text(city) },
                                        onClick = {
                                            viewModel.setCity(city)
                                            cityMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Service, ville ou quartier (ex: Plomberie, Cadjehoun)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = ProxiGreen,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = ProxiDarkBlue)
                        }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2 Main action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateSearchLocation(selectedCountry.code, selectedDepartment, selectedCity)
                                viewModel.updateSearchQuery(searchQuery)
                                viewModel.navigateTo(Screen.Search(countryCode = selectedCountry.code, department = selectedDepartment, city = selectedCity, query = searchQuery))
                            },
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("home_find_pro_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Trouver un pro", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.ProWizard) },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("home_register_pro_button"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Text("Inscrire mon activité", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // --- 2. CATEGORIES SECTION ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Catégories de services",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiTextPrimary
                        )
                        Text(
                            text = "Sélectionnez un métier pour voir les artisans",
                            fontSize = 12.sp,
                            color = ProxiTextSecondary
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.updateSearchLocation(selectedCountry.code, selectedDepartment, selectedCity)
                            viewModel.navigateTo(Screen.Search(countryCode = selectedCountry.code, department = selectedDepartment, city = selectedCity))
                        }
                    ) {
                        Text("Voir tout", color = ProxiDarkBlue, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Grid of top categories
                val displayCats = categories.take(8)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    displayCats.chunked(4).forEach { rowCats ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCats.forEach { cat ->
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("cat_card_${cat.id}")
                                        .clickable {
                                            viewModel.updateSearchCategory(cat.id)
                                            viewModel.updateSearchLocation(selectedCountry.code, selectedDepartment, selectedCity)
                                            viewModel.navigateTo(
                                                Screen.Search(
                                                    categoryId = cat.id,
                                                    countryCode = selectedCountry.code,
                                                    department = selectedDepartment,
                                                    city = selectedCity
                                                )
                                            )
                                        },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 12.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(ProxiDarkBlue.copy(alpha = 0.08f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = CategoryIconHelper.getIcon(cat.iconKey),
                                                contentDescription = cat.name,
                                                tint = ProxiDarkBlue,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = cat.name,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = ProxiTextPrimary,
                                            textAlign = TextAlign.Center,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                            // Fill blank weights if chunk size < 4
                            repeat(4 - rowCats.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // --- 3. PROFESSIONNELS PRÈS DE CHEZ VOUS ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Professionnels à ${selectedCity}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiTextPrimary
                        )
                        Text(
                            text = "Profils vérifiés et disponibles dans votre zone",
                            fontSize = 12.sp,
                            color = ProxiTextSecondary
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.updateSearchLocation(selectedCountry.code, selectedDepartment, selectedCity)
                            viewModel.navigateTo(Screen.Search(countryCode = selectedCountry.code, department = selectedDepartment, city = selectedCity))
                        }
                    ) {
                        Text("Recherche avancée", color = ProxiGreen, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val localPros = approvedProfiles.filter {
                    it.countryCode.equals(selectedCountry.code, ignoreCase = true) &&
                    (selectedCity.isBlank() || it.city.equals(selectedCity, ignoreCase = true)) &&
                    (selectedDepartment.isBlank() || it.department.equals(selectedDepartment, ignoreCase = true))
                }

                if (localPros.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aucun professionnel répertorié pour le moment à ${selectedCity}.",
                                fontSize = 14.sp,
                                color = ProxiTextSecondary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.navigateTo(Screen.ProWizard) },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiDarkBlue)
                            ) {
                                Text("Soyez le premier pro à vous inscrire !")
                            }
                        }
                    }
                } else {
                    localPros.take(3).forEach { pro ->
                        val cat = categories.find { it.id == pro.primaryCategoryId }
                        ProfileCard(
                            profile = pro,
                            category = cat,
                            onViewProfile = { viewModel.selectProfile(pro.id) },
                            onContactWhatsApp = { viewModel.contactViaWhatsApp(context, pro) },
                            onContactPhone = { viewModel.contactViaPhone(context, pro) },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
            }
        }

        // --- 4. COMMENT ÇA MARCHE EN 3 ÉTAPES ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Comment utiliser ProxiAfrik ?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Text(
                        text = "Simple, rapide et sans inscription obligatoire pour les clients.",
                        fontSize = 12.sp,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step 1
                    StepItem(
                        stepNumber = "1",
                        title = "Sélectionnez votre ville",
                        description = "Choisissez votre pays et votre ville, puis tapez le service ou l'artisan recherché."
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2
                    StepItem(
                        stepNumber = "2",
                        title = "Comparez les profils",
                        description = "Consultez les prestations détaillées, les tarifs indicatifs et les zones d'intervention."
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 3
                    StepItem(
                        stepNumber = "3",
                        title = "Contactez directement",
                        description = "Cliquez sur WhatsApp ou Téléphone pour échanger directement sans commission ni intermédiaire."
                    )
                }
            }
        }

        // --- 5. CALL TO ACTION PROFESSIONNELS ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ProxiDarkBlue)
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Vous êtes un professionnel ?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Créez votre fiche d'activité sur ProxiAfrik, gagnez en visibilité dans votre quartier et recevez des appels de clients sérieux.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.ProWizard) },
                        colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Créer ma fiche professionnelle", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- 6. FOOTER & MENTIONS LÉGALES ---
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, start = 16.dp, end = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "ProxiAfrik © 2026 - Plateforme de mise en relation de services en Afrique",
                    fontSize = 12.sp,
                    color = ProxiTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conditions d'utilisation",
                        fontSize = 12.sp,
                        color = ProxiDarkBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Legal(LegalType.CGU)) }
                    )
                    Text(
                        text = "Politique de confidentialité",
                        fontSize = 12.sp,
                        color = ProxiDarkBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Legal(LegalType.PRIVACY)) }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Les tarifs indiqués par les professionnels sont purement indicatifs.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ProxiGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNumber,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ProxiTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = ProxiTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
