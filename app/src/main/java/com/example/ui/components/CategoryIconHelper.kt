package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIcon(key: String): ImageVector {
        return when (key) {
            "plumbing" -> Icons.Default.Plumbing
            "electric_bolt" -> Icons.Default.ElectricBolt
            "build" -> Icons.Default.Build
            "smartphone" -> Icons.Default.Smartphone
            "content_cut" -> Icons.Default.ContentCut
            "style" -> Icons.Default.Style
            "cleaning_services" -> Icons.Default.CleaningServices
            "format_paint" -> Icons.Default.Palette
            "carpenter" -> Icons.Default.Handyman
            "photo_camera" -> Icons.Default.PhotoCamera
            "celebration" -> Icons.Default.Celebration
            "local_shipping" -> Icons.Default.LocalShipping
            "delivery_dining" -> Icons.Default.DeliveryDining
            "school" -> Icons.Default.School
            "computer" -> Icons.Default.Computer
            "translate" -> Icons.Default.Translate
            "business_center" -> Icons.Default.BusinessCenter
            else -> Icons.Default.Work
        }
    }
}
