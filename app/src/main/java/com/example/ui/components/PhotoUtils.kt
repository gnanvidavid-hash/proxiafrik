package com.example.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * Compresses an image selected via the Android Photo Picker (PickVisualMedia)
 * into a compact Base64 JPEG data URL (< 280 KB) for Firestore persistence.
 */
suspend fun encodeImageUriToDataUrl(context: Context, uri: Uri, maxDim: Int = 640): String? =
    withContext(Dispatchers.IO) {
        try {
            val resolver = context.contentResolver
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, bounds)
            }
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

            var sampleSize = 1
            val longest = max(bounds.outWidth, bounds.outHeight)
            while (longest / sampleSize > maxDim * 2) {
                sampleSize *= 2
            }

            val decodeOpts = BitmapFactory.Options().apply { inSampleSize = sampleSize }
            val decoded = resolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream, null, decodeOpts)
            } ?: return@withContext null

            val scaleRatio = max(decoded.width, decoded.height).toFloat() / maxDim.toFloat()
            val scaled = if (scaleRatio > 1f) {
                val targetW = (decoded.width / scaleRatio).toInt().coerceAtLeast(1)
                val targetH = (decoded.height / scaleRatio).toInt().coerceAtLeast(1)
                Bitmap.createScaledBitmap(decoded, targetW, targetH, true).also {
                    if (it !== decoded) decoded.recycle()
                }
            } else {
                decoded
            }

            val baos = ByteArrayOutputStream()
            var quality = 76
            scaled.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            while (baos.size() > 210_000 && quality > 35) {
                baos.reset()
                quality -= 12
                scaled.compress(Bitmap.CompressFormat.JPEG, quality, baos)
            }
            scaled.recycle()

            val base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64"
        } catch (_: Exception) {
            null
        }
    }

@Composable
fun ProxiPhoto(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    fallbackContent: @Composable () -> Unit = {}
) {
    if (imageUrl.startsWith("data:image")) {
        val imageBitmap = remember(imageUrl) {
            try {
                val base64Part = imageUrl.substringAfter(",", "")
                if (base64Part.isBlank()) {
                    null
                } else {
                    val bytes = Base64.decode(base64Part, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                }
            } catch (_: Exception) {
                null
            }
        }
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = modifier
            )
        } else {
            fallbackContent()
        }
    } else if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        fallbackContent()
    }
}
