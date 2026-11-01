package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CategoryEntity
import com.example.ui.components.ProfileCard
import com.example.ui.navigation.Screen
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel

@Composable
fun SearchScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val searchFilter by viewModel.searchFilter.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()

    var countryMenuExpanded by remember { mutableStateOf(false) }
    var departmentMenuExpanded by remember { mutableStateOf(false) }
    var cityMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("search_screen")
    ) {
        // --- Filter Header Surface ---
        Surface(
            color = Color.White,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchFilter.query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Service ou mot-clé (ex: plomberie, meuble)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_query_input"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = ProxiDarkBlue)
                    },
                    trailingIcon = {
                        if (searchFilter.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Effacer")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ProxiGreen,
                        unfocusedBorderColor = ProxiBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Location selector row 1: Pays & Département
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Picker
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProxiBackground)
                                .clickable { countryMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = ProxiDarkBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text("Pays", fontSize = 9.sp, color = ProxiTextSecondary)
                                Text(
                                    text = selectedCountry.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProxiTextPrimary
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

                    // Department Picker
                    Box(modifier = Modifier.weight(1.2f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProxiBackground)
                                .clickable { departmentMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Département / Région", fontSize = 9.sp, color = ProxiTextSecondary)
                                Text(
                                    text = if (searchFilter.department.isNotBlank()) searchFilter.department else "Tous départements",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (searchFilter.department.isNotBlank()) ProxiDarkBlue else ProxiTextSecondary
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
                                    viewModel.updateSearchLocation(selectedCountry.code, "", searchFilter.city, searchFilter.neighborhood)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Location selector row 2: Ville & Quartier
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // City selector badge
                    Box(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProxiBackground)
                                .clickable { cityMenuExpanded = true }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Ville", fontSize = 9.sp, color = ProxiTextSecondary)
                                Text(
                                    text = if (searchFilter.city.isNotBlank()) searchFilter.city else "Toutes villes",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (searchFilter.city.isNotBlank()) ProxiTextPrimary else ProxiTextSecondary
                                )
                            }
                        }

                        val availableCities = if (searchFilter.department.isNotBlank()) {
                            selectedCountry.departments.find { it.name.equals(searchFilter.department, ignoreCase = true) }?.cities ?: selectedCountry.allCities
                        } else {
                            selectedCountry.allCities
                        }

                        DropdownMenu(
                            expanded = cityMenuExpanded,
                            onDismissRequest = { cityMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Toutes les villes") },
                                onClick = {
                                    viewModel.updateSearchLocation(selectedCountry.code, searchFilter.department, "", searchFilter.neighborhood)
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

                    // Quartier filter
                    OutlinedTextField(
                        value = searchFilter.neighborhood,
                        onValueChange = { viewModel.updateSearchLocation(searchFilter.countryCode, searchFilter.department, searchFilter.city, it) },
                        placeholder = { Text("Quartier (ex: Cadjehoun)") },
                        modifier = Modifier.weight(1.2f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ProxiGreen,
                            unfocusedBorderColor = ProxiBorder
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category chips scroll
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = searchFilter.categoryId.isEmpty(),
                            onClick = { viewModel.updateSearchCategory("") },
                            label = { Text("Toutes catégories") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProxiDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(categories) { cat ->
                        FilterChip(
                            selected = searchFilter.categoryId == cat.id,
                            onClick = {
                                if (searchFilter.categoryId == cat.id) {
                                    viewModel.updateSearchCategory("")
                                } else {
                                    viewModel.updateSearchCategory(cat.id)
                                }
                            },
                            label = { Text(cat.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ProxiDarkBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // --- Results Section ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${searchResults.size} professionnel(s) trouvé(s)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiTextPrimary
                    )

                    if (searchFilter.categoryId.isNotEmpty() || searchFilter.query.isNotEmpty() || searchFilter.neighborhood.isNotEmpty() || searchFilter.department.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                viewModel.updateSearchQuery("")
                                viewModel.updateSearchCategory("")
                                viewModel.updateSearchLocation(searchFilter.countryCode, "", "", "")
                            }
                        ) {
                            Text("Réinitialiser filtres", fontSize = 12.sp, color = ProxiDarkBlue)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (searchResults.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Aucun professionnel ne correspond à ces critères.",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProxiTextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Conseils : vérifiez l'orthographe, effacez le filtre par quartier ou choisissez une autre ville dans le sélecteur.",
                                fontSize = 13.sp,
                                color = ProxiTextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.updateSearchCategory("")
                                    viewModel.updateSearchQuery("")
                                    viewModel.updateSearchLocation(searchFilter.countryCode, searchFilter.city, "")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
                            ) {
                                Text("Voir tous les pros de ${searchFilter.city}")
                            }
                        }
                    }
                }
            } else {
                items(searchResults, key = { it.id }) { pro ->
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
}
