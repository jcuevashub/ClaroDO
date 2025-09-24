package com.example.contactsapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AlphabetIndex(
    availableLetters: Set<Char>,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    val alphabet = ('A'..'Z').toList()

    LazyColumn(
        modifier = modifier
            .width(24.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(alphabet.size) { index ->
            val letter = alphabet[index]
            val isAvailable = availableLetters.contains(letter)

            AlphabetIndexItem(
                letter = letter,
                isAvailable = isAvailable,
                onClick = { if (isAvailable) onLetterClick(letter) }
            )
        }
    }
}

@Composable
private fun AlphabetIndexItem(
    letter: Char,
    isAvailable: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isAvailable) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    }

    Text(
        text = letter.toString(),
        fontSize = 10.sp,
        fontWeight = if (isAvailable) FontWeight.Bold else FontWeight.Normal,
        color = textColor,
        modifier = Modifier
            .size(16.dp)
            .clickable(enabled = isAvailable) { onClick() }
            .wrapContentSize()
    )
}