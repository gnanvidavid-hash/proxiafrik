package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryItem
import com.example.data.model.ProfessionalProfile
import com.example.ui.theme.ProxiBorder
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiGreen
import com.example.ui.theme.ProxiGreenText
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary

@Composable
fun ProfileCard(
    profile: ProfessionalProfile,
    category: CategoryItem?,
    onViewProfile: () -> Unit,
    onContactWhatsApp: () -> Unit,
    onContactPhone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_card_${profile.id}")
            .clickable { onViewProfile() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ProxiDarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    ProxiPhoto(
                        imageUrl = profile.profileImage,
                        contentDescription = profile.businessName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        fallbackContent = {
                            val initial = profile.businessName.firstOrNull()?.uppercase() ?: "P"
                            Text(
                                text = initial,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = profile.businessName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ProxiTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        if (profile.verificationStatus == "APPROVED") {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Profil vérifié par l'administrateur",
                                tint = ProxiGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = category?.name ?: "Service professionnel",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProxiDarkBlue
                    )
                }

                if (profile.isSponsored) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFEF3C7))
                            .border(1.dp, Color(0xFFD97706), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Sponsorisé",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = ProxiTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                val locText = buildString {
                    append(profile.city)
                    if (profile.neighborhood.isNotBlank()) {
                        append(" (").append(profile.neighborhood).append(")")
                    }
                    if (profile.department.isNotBlank()) {
                        append(" • ").append(profile.department)
                    }
                    append(", ").append(profile.countryName)
                }
                Text(
                    text = locText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ProxiTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = profile.description,
                fontSize = 14.sp,
                color = ProxiTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            if (profile.interventionZones.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Zone : ${profile.interventionZones}",
                    fontSize = 13.sp,
                    color = ProxiTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(48.dp)
                        .testTag("view_profile_btn_${profile.id}"),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ProxiDarkBlue)
                ) {
                    Text(
                        text = "Voir le profil",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ProxiDarkBlue
                    )
                }

                Button(
                    onClick = onContactWhatsApp,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("whatsapp_btn_${profile.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProxiGreenText,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "WhatsApp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onContactPhone,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("call_btn_${profile.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ProxiDarkBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Appeler",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
