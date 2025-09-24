package com.example.contactsapp.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.domain.model.Contact
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EnhancedContactList(
    contacts: List<Contact>,
    selectedContacts: Set<Contact>,
    isSelectionMode: Boolean,
    onContactClick: (Contact) -> Unit,
    onContactDelete: (Contact) -> Unit,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = contacts,
            key = { it.id }
        ) { contact ->
            SwipeableContactItem(
                contact = contact,
                isSelected = selectedContacts.contains(contact),
                isSelectionMode = isSelectionMode,
                onSelectionToggle = { onContactClick(contact) },
                onDelete = { onContactDelete(contact) },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun SwipeableContactItem(
    contact: Contact,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onSelectionToggle: () -> Unit,
    onDelete: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = LocalHapticFeedback.current
    val density = LocalDensity.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val swipeThreshold = with(density) { 120.dp.toPx() }

    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "swipeOffset"
    )

    val deleteIconAlpha by animateFloatAsState(
        targetValue = if (abs(offsetX) > swipeThreshold * 0.3f) 1f else 0.3f,
        label = "deleteIconAlpha"
    )

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(R.string.action_delete),
                tint = MaterialTheme.colorScheme.error.copy(alpha = deleteIconAlpha),
                modifier = Modifier
                    .size(24.dp)
                    .scale(if (abs(offsetX) > swipeThreshold * 0.5f) 1.2f else 1f)
            )
            Spacer(modifier = Modifier.width(24.dp))
        }

        EnhancedContactItem(
            contact = contact,
            isSelected = isSelected,
            isSelectionMode = isSelectionMode,
            onSelectionToggle = onSelectionToggle,
            modifier = Modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            isDragging = true
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragEnd = {
                            isDragging = false
                            if (abs(offsetX) > swipeThreshold) {
                                onDelete(contact)
                            }
                            coroutineScope.launch {
                                animate(
                                    initialValue = offsetX,
                                    targetValue = 0f,
                                    animationSpec = spring()
                                ) { value, _ ->
                                    offsetX = value
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        val newOffset = (offsetX + dragAmount).coerceIn(-200f, 0f)
                        offsetX = newOffset
                    }
                }
        )
    }
}

@Composable
private fun EnhancedContactItem(
    contact: Contact,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "itemScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(animatedScale)
            .clickable { onSelectionToggle() }
            .semantics {
                contentDescription = "Contact: ${contact.name} ${contact.lastName}"
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DynamicAvatar(
                name = contact.name,
                lastName = contact.lastName,
                imageUrl = contact.imageUrl,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${contact.name} ${contact.lastName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val countryCode = stringResource(R.string.country_code)
                val formattedPhone = remember(contact.phone, countryCode) {
                    formatPhoneNumber(contact.phone, countryCode)
                }

                Text(
                    text = formattedPhone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!isSelectionMode) {
                ContactQuickActions(
                    phoneNumber = contact.phone,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else {
                AnimatedSelectionIndicator(
                    isSelected = isSelected,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedSelectionIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "selectionScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "selectionColor"
    )

    Icon(
        Icons.Default.CheckCircle,
        contentDescription = if (isSelected) stringResource(R.string.selected)
        else stringResource(R.string.not_selected),
        tint = iconColor,
        modifier = modifier.scale(animatedScale)
    )
}

private fun formatPhoneNumber(phone: String, countryCode: String): String {
    val digits = phone.filter { it.isDigit() }
    return if (digits.length >= 2) {
        buildString {
            append(countryCode)
            append(' ')
            if (digits.length >= 4) {
                append(digits.substring(1, 4))
                append('-')
                val rest = digits.substring(4)
                if (rest.length <= 3) {
                    append(rest)
                } else {
                    append(rest.substring(0, 3))
                    append('-')
                    append(rest.substring(3))
                }
            } else {
                append(digits.substring(1))
            }
        }
    } else digits
}