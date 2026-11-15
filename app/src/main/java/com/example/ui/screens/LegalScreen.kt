package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.LegalType
import com.example.ui.theme.ProxiBackground
import com.example.ui.theme.ProxiDarkBlue
import com.example.ui.theme.ProxiTextPrimary
import com.example.ui.theme.ProxiTextSecondary

@Composable
fun LegalScreen(
    type: LegalType,
    onNavigateBack: (() -> Unit)? = null
) {
    val isCgu = type == LegalType.CGU

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("legal_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (onNavigateBack != null) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateBack() }
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                        .testTag("legal_back_button")
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
                        fontWeight = FontWeight.SemiBold,
                        color = ProxiDarkBlue
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (isCgu) "Conditions d'utilisation" else "Politique de confidentialité",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ProxiAfrik — Plateforme africaine de mise en relation de proximité",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    if (isCgu) {
                        LegalSection(
                            title = "1. Objet de la plateforme",
                            content = "ProxiAfrik facilite la recherche et la mise en relation entre des prestataires de services qualifiés (artisans, techniciens, indépendants) et des clients en Afrique. La plateforme n'intervient pas dans la facturation entre le client et le professionnel."
                        )

                        LegalSection(
                            title = "2. Rôles et comptes utilisateurs",
                            content = "Chaque compte est authentifié de façon sécurisée via Google et associé à un rôle stocké en base de données (client, professionnel ou administrateur). Aucune élévation de privilège n'est possible sans validation administrative."
                        )

                        LegalSection(
                            title = "3. Validation des profils professionnels",
                            content = "Toute fiche professionnelle soumise est placée en statut « En attente » et reste invisible dans la recherche publique jusqu'à vérification et approbation explicite par un administrateur ProxiAfrik."
                        )

                        LegalSection(
                            title = "4. Tarifs, devis et signalements",
                            content = "Les tarifs affichés sont purement indicatifs ; le devis final est convenu directement avec le professionnel. Tout utilisateur peut signaler un profil suspect ou erroné afin qu'il soit examiné et, si nécessaire, suspendu par l'administration."
                        )
                    } else {
                        LegalSection(
                            title = "1. Données collectées",
                            content = "ProxiAfrik collecte uniquement les données nécessaires à l'authentification (nom, adresse e-mail Google) et, pour les professionnels qui s'inscrivent, les informations d'activité déclarées (description, localisation, prestations, photos, numéro WhatsApp et téléphone professionnel)."
                        )

                        LegalSection(
                            title = "2. Consentement et visibilité publique",
                            content = "Les coordonnées téléphoniques et WhatsApp d'un professionnel ne sont publiées qu'après son consentement explicite lors de l'inscription et validation du profil par un administrateur. Les fiches en attente ou refusées ne sont accessibles qu'à leur propriétaire et aux administrateurs."
                        )

                        LegalSection(
                            title = "3. Sécurité et contrôle d'accès",
                            content = "Toutes les lectures et écritures sont protégées par des règles de sécurité Firestore strictes isolant les données privées de chaque compte et réservant la modération aux administrateurs authentifiés."
                        )

                        LegalSection(
                            title = "4. Vos droits (accès et suppression)",
                            content = "Vous pouvez à tout moment modifier vos informations professionnelles, mettre à jour votre rôle Client/Pro ou demander la suppression de votre fiche et de vos données personnelles auprès de l'équipe ProxiAfrik."
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalSection(
    title: String,
    content: String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ProxiDarkBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text(content, fontSize = 14.sp, color = ProxiTextPrimary, lineHeight = 20.sp)
    }
}
