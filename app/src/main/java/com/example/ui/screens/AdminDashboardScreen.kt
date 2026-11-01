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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.ProfessionalProfileEntity
import com.example.data.local.ReportEntity
import com.example.ui.components.CategoryIconHelper
import com.example.ui.theme.ProxiAmber
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiRed
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary
import com.example.ui.viewmodel.ProxiAfrikViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val pendingProfiles by viewModel.pendingProfiles.collectAsState()
    val allProfiles by viewModel.allProfilesForAdmin.collectAsState()
    val allCategories by viewModel.allCategories.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Rejection Dialog state
    var profileToReject by remember { mutableStateOf<ProfessionalProfileEntity?>(null) }
    var rejectionReasonText by remember { mutableStateOf("") }

    // Add category dialog state
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCatName by remember { mutableStateOf("") }
    var newCatDesc by remember { mutableStateOf("") }

    val approvedCount = allProfiles.count { it.verificationStatus == "APPROVED" }
    val pendingCount = pendingProfiles.size
    val suspendedCount = allProfiles.count { it.verificationStatus == "SUSPENDED" }
    val unresolvedReportsCount = allReports.count { it.status == "PENDING" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("admin_dashboard_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        // --- 1. ADMIN HEADER & KPIS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ProxiDarkBlue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = ProxiAmber, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Panneau d'Administration ProxiAfrik", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Text("Supervision des inscriptions, modération et annuaire", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    // KPIs row 1
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiBox(label = "Approuvés", value = "$approvedCount", color = ProxiGreen, modifier = Modifier.weight(1f))
                        KpiBox(label = "En attente", value = "$pendingCount", color = ProxiAmber, modifier = Modifier.weight(1f))
                        KpiBox(label = "Suspendus", value = "$suspendedCount", color = ProxiRed, modifier = Modifier.weight(1f))
                        KpiBox(label = "Signalements", value = "$unresolvedReportsCount", color = Color(0xFFF97316), modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // --- 2. TABS ---
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = ProxiDarkBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Validations ($pendingCount)", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Catégories", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Signalements ($unresolvedReportsCount)", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Audit", fontSize = 12.sp) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        // --- 3. TAB CONTENT ---
        when (selectedTab) {
            0 -> {
                // VALIDATIONS TAB
                if (pendingProfiles.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = ProxiGreen, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Aucun profil en attente de validation.", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Tous les profils soumis ont été examinés.", color = ProxiTextSecondary, fontSize = 13.sp)
                            }
                        }
                    }
                } else {
                    items(pendingProfiles, key = { it.id }) { pro ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .testTag("admin_pending_item_${pro.id}"),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(pro.businessName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ProxiDarkBlue)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("En attente", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text("${pro.city} (${pro.neighborhood}), ${pro.countryName}", fontSize = 13.sp, color = ProxiTextSecondary)
                                Text("Tel : ${pro.phonePublic} | WhatsApp : ${pro.whatsappNumber}", fontSize = 12.sp, color = ProxiTextPrimary)

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(pro.description, fontSize = 13.sp, color = ProxiTextPrimary, maxLines = 3)

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action Buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.approveProfile(pro.id)
                                            Toast.makeText(context, "${pro.businessName} approuvé avec succès !", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen),
                                        modifier = Modifier.weight(1f).testTag("admin_approve_btn_${pro.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Approuver")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            profileToReject = pro
                                            rejectionReasonText = "Pièces non conformes ou numéro non joignable"
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ProxiRed),
                                        modifier = Modifier.weight(1f).testTag("admin_reject_btn_${pro.id}"),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Refuser")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // CATEGORIES TAB
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Gestion des Catégories (${allCategories.size})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        TextButton(onClick = { showAddCategoryDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter catégorie", color = ProxiGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(allCategories, key = { it.id }) { cat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    CategoryIconHelper.getIcon(cat.iconKey),
                                    contentDescription = null,
                                    tint = if (cat.isActive) ProxiDarkBlue else Color.Gray,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(cat.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(cat.description, fontSize = 11.sp, color = ProxiTextSecondary, maxLines = 1)
                                }
                            }

                            Button(
                                onClick = {
                                    viewModel.toggleCategoryActive(cat.id, cat.isActive)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (cat.isActive) ProxiGreen else Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(if (cat.isActive) "Active" else "Désactivée", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            2 -> {
                // SIGNALEMENTS TAB
                if (allReports.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Aucun signalement d'utilisateur.", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                } else {
                    items(allReports, key = { it.id }) { report ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Motif : ${report.reason}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProxiRed)
                                    Text(report.status, fontSize = 11.sp, color = if (report.status == "PENDING") ProxiAmber else ProxiGreen)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Détails : ${report.description}", fontSize = 13.sp, color = ProxiTextPrimary)
                                Text("Signalé par : ${report.reporterName}", fontSize = 11.sp, color = ProxiTextSecondary)

                                if (report.status == "PENDING") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                viewModel.resolveReport(report.id, "RESOLVED")
                                                Toast.makeText(context, "Signalement résolu", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
                                        ) {
                                            Text("Classer comme résolu", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.suspendProfile(report.reportedProfileId, "Suite à signalement fondé")
                                                viewModel.resolveReport(report.id, "SUSPENDED")
                                                Toast.makeText(context, "Profil suspendu", Toast.LENGTH_SHORT).show()
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = ProxiRed)
                                        ) {
                                            Text("Suspendre profil", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // AUDIT LOGS TAB
                item {
                    Text("Journal d'Audit des Actions Administratives", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(auditLogs, key = { it.id }) { log ->
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val dateStr = dateFormat.format(Date(log.createdAt))

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(log.action, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ProxiDarkBlue)
                                Text(dateStr, fontSize = 11.sp, color = ProxiTextSecondary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.details, fontSize = 12.sp, color = ProxiTextPrimary)
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog with Mandatory Reason
    if (profileToReject != null) {
        val pro = profileToReject!!
        AlertDialog(
            onDismissRequest = { profileToReject = null },
            title = { Text("Refuser la validation") },
            text = {
                Column {
                    Text("Veuillez indiquer le motif du refus pour ${pro.businessName} :")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectionReasonText,
                        onValueChange = { rejectionReasonText = it },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (rejectionReasonText.isNotBlank()) {
                            viewModel.rejectProfile(pro.id, rejectionReasonText.trim())
                            profileToReject = null
                            Toast.makeText(context, "Profil refusé.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiRed)
                ) {
                    Text("Confirmer le refus")
                }
            },
            dismissButton = {
                TextButton(onClick = { profileToReject = null }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Créer une nouvelle catégorie") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newCatName,
                        onValueChange = { newCatName = it },
                        label = { Text("Nom de la catégorie *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCatDesc,
                        onValueChange = { newCatDesc = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCatName.isNotBlank()) {
                            viewModel.addNewCategory(newCatName, newCatDesc, "work")
                            showAddCategoryDialog = false
                            newCatName = ""
                            newCatDesc = ""
                            Toast.makeText(context, "Catégorie ajoutée avec succès", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen)
                ) {
                    Text("Créer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
private fun KpiBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White.copy(alpha = 0.85f), fontSize = 10.sp)
        }
    }
}
