package com.example.contactsapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants
import kotlin.math.abs

@Composable
fun DynamicAvatar(
    name: String,
    lastName: String,
    imageUrl: String,
    size: Dp = 56.dp,
    modifier: Modifier = Modifier
) {
    val hasImage = imageUrl.isNotBlank()

    if (hasImage) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.photo_of, name),
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        val initials = remember(name, lastName) {
            listOfNotNull(name, lastName)
                .filter { it.isNotBlank() }
                .map { it.trim().firstOrNull()?.uppercase() ?: StringConstants.EMPTY_STRING }
                .take(2)
                .joinToString(StringConstants.EMPTY_STRING)
                .ifBlank { "?" }
        }

        val avatarColors = remember(name, lastName) {
            generateAvatarColors(name + lastName)
        }

        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = avatarColors
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateAvatarColors(fullName: String): List<Color> {
    val hash = abs(fullName.hashCode())
    val hue = (hash % 360).toFloat()

    val primaryColor = Color.hsl(hue, 0.6f, 0.5f)
    val secondaryColor = Color.hsl((hue + 30) % 360, 0.7f, 0.4f)

    return listOf(primaryColor, secondaryColor)
}