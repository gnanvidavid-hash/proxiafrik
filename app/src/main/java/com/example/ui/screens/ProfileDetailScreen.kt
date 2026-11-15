package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PortfolioPhoto
import com.example.data.model.ReviewItem
import com.example.ui.components.CategoryIconHelper
import com.example.ui.components.ProxiPhoto
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    viewModel: ProxiAfrikViewModel
) {
    val context = LocalContext.current
    val pro by viewModel.selectedProfile.collectAsState()
    val services by viewModel.selectedProfileServices.collectAsState()
    val portfolio by viewModel.selectedProfilePortfolio.collectAsState()
    val reviews by viewModel.selectedProfileReviews.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }
    var reportReason by remember { mutableStateOf("Coordonnées incorrectes") }
    var reportDetails by remember { mutableStateOf("") }

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewerName by remember { mutableStateOf("") }
    var reviewComment by remember { mutableStateOf("") }

    var selectedPortfolioImage by remember { mutableStateOf<PortfolioPhoto?>(null) }

    if (pro == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProxiBackground)
                .testTag("profile_detail_loading"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Chargement du profil...",
                color = ProxiTextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        return
    }

    val currentPro = pro!!
    val category = categories.find { it.id == currentPro.primaryCategoryId }

    val reviewCount = reviews.size
    val averageRating = if (reviews.isEmpty()) 5.0 else reviews.map { it.rating }.average()
    val formattedAverage = String.format(Locale.US, "%.1f", averageRating)

    val starCounts = remember(reviews) {
        (5 downTo 1).associateWith { star -> reviews.count { it.rating == star } }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("profile_detail_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 0. TOP ACTION BAR ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { viewModel.navigateBack() }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .testTag("profile_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = ProxiDarkBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Retour",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val shareText = "Découvrez ${currentPro.businessName} sur ProxiAfrik (${currentPro.city}, ${currentPro.countryName}) : Téléphone : ${currentPro.phonePublic} / WhatsApp : ${currentPro.whatsappNumber}"
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partager ce profil"))
                        },
                        modifier = Modifier.testTag("share_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Partager la fiche",
                            tint = ProxiDarkBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.testTag("report_profile_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Signaler ce profil",
                            tint = ProxiRed,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // --- 1. HERO IDENTITY CARD ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(ProxiDarkBlue, Color(0xFF2C5282))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            ProxiPhoto(
                                imageUrl = currentPro.profileImage,
                                contentDescription = currentPro.businessName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                fallbackContent = {
                                    Text(
                                        text = currentPro.businessName.firstOrNull()?.uppercase() ?: "P",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = currentPro.businessName,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProxiTextPrimary
                                )
                                if (currentPro.verificationStatus == "APPROVED") {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Profil vérifié par ProxiAfrik",
                                        tint = ProxiGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ProxiDarkBlue.copy(alpha = 0.09f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                val catIcon = CategoryIconHelper.getIcon(category?.iconKey ?: "")
                                Icon(
                                    imageVector = catIcon,
                                    contentDescription = null,
                                    tint = ProxiDarkBlue,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = category?.name ?: "Prestataire de services",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ProxiDarkBlue
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ProxiBackground)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = ProxiAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$formattedAverage / 5",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProxiTextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "($reviewCount avis)",
                                fontSize = 13.sp,
                                color = ProxiTextSecondary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                tint = ProxiTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentPro.viewsCount} vues",
                                fontSize = 13.sp,
                                color = ProxiTextSecondary
                            )
                        }

                        if (currentPro.verificationStatus == "APPROVED") {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ProxiGreen.copy(alpha = 0.14f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Vérifié",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProxiGreenText
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.contactViaWhatsApp(context, currentPro) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_whatsapp_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ProxiGreenText,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "WhatsApp",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = { viewModel.contactViaPhone(context, currentPro) },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("detail_call_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ProxiDarkBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Appeler",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // --- 2. CONTACT INFORMATION & GEOGRAPHY ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("contact_information_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Coordonnées & Localisation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ProxiBackground)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ProxiGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = ProxiGreenText,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Téléphone direct",
                                    fontSize = 12.sp,
                                    color = ProxiTextSecondary
                                )
                                Text(
                                    text = currentPro.phonePublic,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProxiTextPrimary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Numéro de téléphone", currentPro.phonePublic)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Numéro copié : ${currentPro.phonePublic}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_phone_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier le numéro",
                                tint = ProxiDarkBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ProxiGreen.copy(alpha = 0.08f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(ProxiGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "WA",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ProxiGreenText
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "WhatsApp professionnel",
                                    fontSize = 12.sp,
                                    color = ProxiTextSecondary
                                )
                                Text(
                                    text = currentPro.whatsappNumber,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ProxiGreenText
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Numéro WhatsApp", currentPro.whatsappNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Numéro WhatsApp copié : ${currentPro.whatsappNumber}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_whatsapp_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copier WhatsApp",
                                tint = ProxiGreenText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = ProxiDarkBlue,
                            modifier = Modifier.size(18.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            val fullLoc = buildString {
                                if (currentPro.neighborhood.isNotBlank()) {
                                    append(currentPro.neighborhood).append(", ")
                                }
                                append(currentPro.city)
                                if (currentPro.department.isNotBlank()) {
                                    append(" • ").append(currentPro.department)
                                }
                                append(", ").append(currentPro.countryName)
                            }
                            Text(
                                text = fullLoc,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ProxiTextPrimary
                            )
                            Text(
                                text = "Localisation principale déclarée",
                                fontSize = 12.sp,
                                color = ProxiTextSecondary
                            )
                        }
                    }

                    if (currentPro.interventionZones.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Zones d'intervention :",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ProxiDarkBlue
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        val zones = currentPro.interventionZones.split(",", ";").map { it.trim() }.filter { it.isNotBlank() }
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(zones) { zone ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ProxiDarkBlue.copy(alpha = 0.08f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = zone,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ProxiDarkBlue
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = ProxiBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = ProxiGreenText,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Horaires : ${currentPro.openingHours.ifBlank { "Sur rendez-vous" }}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = ProxiTextPrimary
                        )
                    }
                }
            }
        }

        // --- 3. ABOUT ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "À propos de l'activité",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPro.description,
                        fontSize = 14.sp,
                        color = ProxiTextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // --- 4. PORTFOLIO ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portfolio_gallery_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Galerie de réalisations",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ProxiDarkBlue
                            )
                            Text(
                                text = "${portfolio.size} photo(s) de chantiers et travaux",
                                fontSize = 13.sp,
                                color = ProxiTextSecondary
                            )
                        }

                        if (portfolio.isNotEmpty()) {
                            Text(
                                text = "Agrandir",
                                fontSize = 13.sp,
                                color = ProxiGreenText,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (portfolio.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ProxiBackground)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aucune photo de réalisation publiée pour l'instant.",
                                fontSize = 13.sp,
                                color = ProxiTextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            portfolio.forEachIndexed { index, item ->
                                PortfolioItemCard(
                                    item = item,
                                    index = index,
                                    onClick = { selectedPortfolioImage = item }
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 5. SERVICES & TARIFS ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Prestations et tarifs indicatifs",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFF92400E),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Les tarifs indiqués sont indicatifs. Les devis finaux sont convenus directement avec le professionnel.",
                                fontSize = 12.sp,
                                color = Color(0xFF78350F),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (services.isEmpty()) {
                        Text(
                            text = "Prestations sur devis personnalisé.",
                            fontSize = 14.sp,
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
                                    Text(
                                        text = srv.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ProxiTextPrimary
                                    )
                                    if (srv.description.isNotBlank()) {
                                        Text(
                                            text = srv.description,
                                            fontSize = 13.sp,
                                            color = ProxiTextSecondary
                                        )
                                    }
                                }

                                val priceLabel = when (srv.pricingType) {
                                    "FIXED" -> "${srv.indicativePrice.toInt()} ${srv.currencyCode}"
                                    "STARTING_AT" -> "À partir de ${srv.indicativePrice.toInt()} ${srv.currencyCode}"
                                    else -> "Sur devis"
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ProxiGreen.copy(alpha = 0.12f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = priceLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ProxiGreenText
                                    )
                                }
                            }
                            if (index < services.size - 1) {
                                HorizontalDivider(color = ProxiBorder)
                            }
                        }
                    }
                }
            }
        }

        // --- 6. RATINGS & REVIEWS ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("customer_ratings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notes & avis clients",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiDarkBlue
                        )

                        TextButton(
                            onClick = { showReviewDialog = true },
                            modifier = Modifier.testTag("open_review_dialog_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.RateReview,
                                contentDescription = null,
                                tint = ProxiGreenText,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Donner un avis",
                                color = ProxiGreenText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ProxiBackground)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text(
                                text = formattedAverage,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = ProxiDarkBlue
                            )
                            Row {
                                repeat(5) { i ->
                                    val isFilled = i < averageRating.toInt()
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (isFilled) ProxiAmber else Color(0xFFCBD5E1),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$reviewCount avis",
                                fontSize = 12.sp,
                                color = ProxiTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            (5 downTo 1).forEach { star ->
                                val count = starCounts[star] ?: 0
                                val fraction = if (reviewCount == 0) 0f else count.toFloat() / reviewCount.toFloat()
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${star}★",
                                        fontSize = 12.sp,
                                        color = ProxiTextSecondary,
                                        modifier = Modifier.width(24.dp)
                                    )
                                    LinearProgressIndicator(
                                        progress = { fraction },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = ProxiAmber,
                                        trackColor = Color(0xFFE2E8F0)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "$count",
                                        fontSize = 12.sp,
                                        color = ProxiTextSecondary,
                                        modifier = Modifier.width(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = ProxiGreenText,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Avis contrôlés selon la charte qualité ProxiAfrik",
                            fontSize = 12.sp,
                            color = ProxiTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = ProxiBorder)
                    Spacer(modifier = Modifier.height(14.dp))

                    if (reviews.isEmpty()) {
                        Text(
                            text = "Aucun avis pour l'instant. Soyez le premier client à partager votre expérience !",
                            fontSize = 13.sp,
                            color = ProxiTextSecondary
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            reviews.forEach { rev ->
                                CustomerReviewItem(review = rev)
                            }
                        }
                    }
                }
            }
        }

        // --- 7. EXPLICIT "SIGNALER CE PROFIL" ACTION ---
        item {
            OutlinedButton(
                onClick = { showReportDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("report_profile_button"),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ProxiRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    tint = ProxiRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Signaler ce profil",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ProxiRed
                )
            }
        }
    }

    selectedPortfolioImage?.let { photo ->
        Dialog(onDismissRequest = { selectedPortfolioImage = null }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ProxiDarkBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        ProxiPhoto(
                            imageUrl = photo.imageUrl,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = photo.caption,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Réalisation de ${currentPro.businessName}",
                        fontSize = 13.sp,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                selectedPortfolioImage = null
                                viewModel.contactViaWhatsApp(context, currentPro)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ProxiGreenText,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Devis WhatsApp", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { selectedPortfolioImage = null },
                            modifier = Modifier.weight(0.7f)
                        ) {
                            Text("Fermer", fontSize = 13.sp, color = ProxiDarkBlue)
                        }
                    }
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("Signaler ce profil", fontWeight = FontWeight.Bold, color = ProxiDarkBlue) },
            text = {
                Column {
                    Text(
                        text = "Signalez toute irrégularité (faux numéro, comportement inapproprié, arnaque) :",
                        fontSize = 13.sp,
                        color = ProxiTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = reportReason,
                        onValueChange = { reportReason = it },
                        label = { Text("Motif du signalement") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reportDetails,
                        onValueChange = { reportDetails = it },
                        label = { Text("Détails") },
                        placeholder = { Text("Décrivez le problème constaté...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.submitReport(
                            currentPro.id,
                            reportReason.trim().ifBlank { "Signalement utilisateur" },
                            reportDetails.trim()
                        )
                        showReportDialog = false
                        reportDetails = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiRed, contentColor = Color.White)
                ) {
                    Text("Envoyer le signalement", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text("Annuler", color = ProxiTextSecondary)
                }
            }
        )
    }

    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = {
                Text(
                    text = "Laisser un avis client",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = ProxiDarkBlue
                )
            },
            text = {
                Column {
                    Text(
                        text = "Notez la prestation de ${currentPro.businessName} :",
                        fontSize = 13.sp,
                        color = ProxiTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        (1..5).forEach { star ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "$star étoiles",
                                tint = if (star <= reviewRating) ProxiAmber else Color(0xFFCBD5E1),
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable { reviewRating = star }
                                    .padding(4.dp)
                                    .testTag("star_select_$star")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reviewerName,
                        onValueChange = { reviewerName = it },
                        label = { Text("Votre nom (facultatif)") },
                        placeholder = { Text("Ex: Aïcha D.") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Votre commentaire *") },
                        placeholder = { Text("Qualité du travail, ponctualité, respect du devis...") },
                        modifier = Modifier.fillMaxWidth().testTag("review_comment_input"),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewComment.trim().length >= 2) {
                            viewModel.submitReview(
                                profileId = currentPro.id,
                                rating = reviewRating,
                                comment = reviewComment.trim(),
                                customAuthorName = reviewerName.trim()
                            )
                            reviewerName = ""
                            reviewComment = ""
                            reviewRating = 5
                            showReviewDialog = false
                        } else {
                            Toast.makeText(context, "Veuillez écrire un commentaire.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ProxiGreen, contentColor = Color.White),
                    modifier = Modifier.testTag("submit_review_button")
                ) {
                    Text("Publier mon avis", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Annuler", color = ProxiTextSecondary)
                }
            }
        )
    }
}

@Composable
fun PortfolioItemCard(
    item: PortfolioPhoto,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("portfolio_item_$index"),
        colors = CardDefaults.cardColors(containerColor = ProxiBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(ProxiDarkBlue.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                ProxiPhoto(
                    imageUrl = item.imageUrl,
                    contentDescription = item.caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Agrandir",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = item.caption,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ProxiTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Réalisation publiée",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ProxiGreenText
                    )
                    Text(
                        text = "Touchez pour agrandir",
                        fontSize = 12.sp,
                        color = ProxiTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerReviewItem(review: ReviewItem) {
    val reviewTimeMs = review.createdAt?.toDate()?.time ?: System.currentTimeMillis()
    val formattedDate = remember(reviewTimeMs) {
        val diff = System.currentTimeMillis() - reviewTimeMs
        val days = diff / (1000 * 60 * 60 * 24)
        when {
            days <= 0 -> "Aujourd'hui"
            days == 1L -> "Hier"
            days < 30 -> "Il y a $days jours"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.FRENCH).format(Date(reviewTimeMs))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(ProxiBackground)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(ProxiDarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = review.authorName.firstOrNull()?.uppercase() ?: "C",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = review.authorName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiTextPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Client vérifié",
                            tint = ProxiGreen,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = formattedDate,
                        fontSize = 12.sp,
                        color = ProxiTextSecondary
                    )
                }
            }

            Row {
                repeat(review.rating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = ProxiAmber,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = review.comment,
            fontSize = 14.sp,
            color = ProxiTextPrimary,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                tint = ProxiGreenText,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Recommande ce professionnel",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ProxiGreenText
            )
        }
    }
}
