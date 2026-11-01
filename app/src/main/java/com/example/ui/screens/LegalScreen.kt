package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    type: LegalType
) {
    val isCgu = type == LegalType.CGU

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ProxiBackground)
            .testTag("legal_screen"),
        contentPadding = PaddingValues(16.dp)
    ) {
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
                        text = if (isCgu) "Conditions Générales d'Utilisation (CGU)" else "Politique de Confidentialité",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ProxiDarkBlue
                    )
                    Text(
                        text = "ProxiAfrik - Plateforme africaine de mise en relation de proximité",
                        fontSize = 12.sp,
                        color = ProxiTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isCgu) {
                        LegalSection(
                            title = "1. Objet de la plateforme",
                            content = "ProxiAfrik a pour objet de faciliter la recherche et la mise en relation entre des prestataires de services professionnels (artisans, techniciens, indépendants) et des clients potentiels en Afrique. L'accès à l'annuaire est entièrement gratuit pour les clients et ne requiert aucune création de compte obligatoire."
                        )

                        LegalSection(
                            title = "2. Nature des tarifs et devis",
                            content = "Les tarifs indiqués sur les profils des professionnels sont purement indicatifs et fournis sous leur propre responsabilité. Les conditions d'intervention, devis finaux et modalités de paiement doivent être convenus directement et d'un commun accord entre le client et le professionnel avant tout début de prestation."
                        )

                        LegalSection(
                            title = "3. Vérification des profils et responsabilité",
                            content = "L'équipe administrative de ProxiAfrik procède à une revue des fiches d'activité avant publication. Toutefois, sauf mention expresse d'audit physique certifié, ProxiAfrik ne saurait garantir l'exactitude absolue des compétences ou de l'identité des prestataires. Les utilisateurs sont invités à faire preuve de discernement et à signaler tout comportement suspect."
                        )

                        LegalSection(
                            title = "4. Signalements et modération",
                            content = "Tout utilisateur peut signaler un profil en cas de coordonnées erronées, abus ou escroquerie. L'équipe ProxiAfrik se réserve le droit de suspendre ou supprimer tout compte ne respectant pas les règles de déontologie."
                        )
                    } else {
                        LegalSection(
                            title = "1. Données collectées",
                            content = "ProxiAfrik collecte uniquement les données nécessaires au bon fonctionnement du service : nom, coordonnées téléphoniques professionnelles déclarées, ville, quartier et descriptif des prestations pour les professionnels ; et informations de contact pour les utilisateurs enregistrés."
                        )

                        LegalSection(
                            title = "2. Consentement à la publication",
                            content = "Les numéros de téléphone et WhatsApp des professionnels ne sont affichés publiquement qu'après recueil de leur consentement explicite lors de la soumission de leur fiche d'activité."
                        )

                        LegalSection(
                            title = "3. Mesure d'audience et statistiques",
                            content = "ProxiAfrik mesure le nombre d'affichages de profils et les clics sur les boutons de contact afin de fournir aux professionnels des statistiques réelles sur leur visibilité, sans collecter de données personnelles intrusives."
                        )

                        LegalSection(
                            title = "4. Droit d'accès et de suppression",
                            content = "Conformément aux réglementations sur la protection des données personnelles, tout utilisateur peut demander la modification ou la suppression définitive de sa fiche et de son compte en contactant le support ProxiAfrik."
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
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ProxiDarkBlue)
        Spacer(modifier = Modifier.height(4.dp))
        Text(content, fontSize = 13.sp, color = ProxiTextPrimary, lineHeight = 18.sp)
    }
}
