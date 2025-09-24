package com.example.contactsapp.presentation.createcontact

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.presentation.components.DynamicAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCreateContactScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val nameRequester = remember { FocusRequester() }
    val lastNameRequester = remember { FocusRequester() }
    val phoneRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                EnhancedTopBar(
                    onNavigateBack = onNavigateBack,
                    onSave = {
                        keyboardController?.hide()
                        viewModel.saveContact()
                    },
                    isSaving = uiState.isSaving,
                    isFormValid = uiState.isFormValid
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.surface
        ) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedImageSelector(
                            imageUrl = uiState.imageUrl,
                            name = uiState.name,
                            lastName = uiState.lastName,
                            onRefreshImage = viewModel::refreshImage
                        )
                    }
                }

                item {
                    ContactFormCard {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            EnhancedTextField(
                                value = uiState.name,
                                onValueChange = viewModel::updateName,
                                label = stringResource(R.string.name),
                                leadingIcon = Icons.Default.Person,
                                errorText = uiState.nameError,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { lastNameRequester.requestFocus() }
                                ),
                                focusRequester = nameRequester
                            )

                            EnhancedTextField(
                                value = uiState.lastName,
                                onValueChange = viewModel::updateLastName,
                                label = stringResource(R.string.last_name),
                                leadingIcon = Icons.Default.Person,
                                errorText = uiState.lastNameError,
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { phoneRequester.requestFocus() }
                                ),
                                focusRequester = lastNameRequester
                            )

                            EnhancedTextField(
                                value = if (uiState.formattedPhone.isNotEmpty()) uiState.formattedPhone else uiState.phone,
                                onValueChange = viewModel::updatePhone,
                                label = stringResource(R.string.phone),
                                leadingIcon = Icons.Default.Phone,
                                errorText = uiState.phoneError,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Phone,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        keyboardController?.hide()
                                        if (uiState.name.isNotBlank() &&
                                            uiState.lastName.isNotBlank() &&
                                            uiState.phone.isNotBlank()) {
                                            viewModel.saveContact()
                                        }
                                    }
                                ),
                                focusRequester = phoneRequester,
                                supportingText = stringResource(R.string.country_code) + " " +
                                               stringResource(R.string.valid_area_codes),
                                placeholder = "809-123-4567"
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedSaveButton(
                            onClick = {
                                keyboardController?.hide()
                                viewModel.saveContact()
                            },
                            isLoading = uiState.isSaving,
                            isEnabled = uiState.name.isNotBlank() &&
                                       uiState.lastName.isNotBlank() &&
                                       uiState.phone.isNotBlank()
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

    }

    uiState.error?.let { error ->
        val errorMessage = when (error) {
            StringConstants.ERR_UNKNOWN -> stringResource(R.string.err_unknown)
            else -> error
        }
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                actionLabel = "OK"
            )
            viewModel.clearError()
        }
    }
}

@Composable
private fun calculateCompletionPercentage(uiState: CreateContactUiState): Float {
    var completedFields = 0
    val totalFields = 3

    if (uiState.name.trim().isNotEmpty() && uiState.nameError == null) completedFields++
    if (uiState.lastName.trim().isNotEmpty() && uiState.lastNameError == null) completedFields++
    if (uiState.phone.isNotEmpty() && uiState.phoneError == null) completedFields++

    return completedFields.toFloat() / totalFields
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedTopBar(
    onNavigateBack: () -> Unit,
    onSave: () -> Unit,
    isSaving: Boolean,
    isFormValid: Boolean
) {
    val saveButtonColor by animateColorAsState(
        targetValue = if (isFormValid) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        label = "saveButtonColor"
    )

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.new_contact),
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            TextButton(
                onClick = onNavigateBack,
                enabled = !isSaving
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        actions = {
            TextButton(
                onClick = onSave,
                enabled = isFormValid && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save),
                        color = saveButtonColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EnhancedImageSelector(
    imageUrl: String,
    name: String,
    lastName: String,
    onRefreshImage: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableStateOf(0f) }

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "imageScale"
    )

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "imageRotation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .scale(animatedScale)
                    .rotate(animatedRotation)
                    .shadow(
                        elevation = if (isPressed) 4.dp else 12.dp,
                        shape = CircleShape,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = {
                            isPressed = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            rotationAngle += 180f
                            onRefreshImage()
                        }
                    )
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.photo_of, name),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else if (name.isNotBlank() || lastName.isNotBlank()) {
                        DynamicAvatar(
                            name = name,
                            lastName = lastName,
                            imageUrl = "",
                            size = 140.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                        ),
                                        radius = 200f
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    rotationAngle += 180f
                    onRefreshImage()
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-6).dp, y = (-6).dp)
                    .size(40.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.tap_image_change),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.tap_image_change),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Toca el ícono de actualizar para cambiar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

@Composable
private fun ContactFormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(28.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            content = content
        )
    }
}

@Composable
private fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() },
    supportingText: String? = null,
    placeholder: String? = null
) {
    val haptic = LocalHapticFeedback.current
    var isFocused by remember { mutableStateOf(false) }
    val isValid = value.trim().isNotEmpty() && errorText == null

    val borderColor by animateColorAsState(
        targetValue = when {
            errorText != null -> MaterialTheme.colorScheme.error
            isValid -> MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        },
        animationSpec = tween(300),
        label = "borderColor"
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            errorText != null -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
            isValid -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.08f)
            isFocused -> MaterialTheme.colorScheme.surfaceContainer
            else -> MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
        label = "containerColor"
    )

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue != value) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onValueChange(newValue)
            },
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            },
            placeholder = if (placeholder != null) {
                {
                    Text(
                        placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else null,
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = when {
                        errorText != null -> MaterialTheme.colorScheme.error
                        isValid -> MaterialTheme.colorScheme.primary
                        isFocused -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            },
            trailingIcon = if (isValid) {
                {
                    AnimatedVisibility(
                        visible = true,
                        enter = scaleIn() + fadeIn(),
                        exit = scaleOut() + fadeOut()
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Valid",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged {
                    if (it.isFocused != isFocused) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    isFocused = it.isFocused
                },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            isError = errorText != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor.copy(alpha = 0.7f),
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor.copy(alpha = 0.8f),
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                focusedLabelColor = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedLabelColor = if (isValid) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(20.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            )
        )

        AnimatedVisibility(
            visible = errorText != null,
            enter = slideInVertically { -it / 2 } + fadeIn(),
            exit = slideOutVertically { -it / 2 } + fadeOut()
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = errorText ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (supportingText != null && errorText == null) {
            Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 6.dp),
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun AnimatedSaveButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    isEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current

    val animatedWidth by animateDpAsState(
        targetValue = if (isLoading) 64.dp else 280.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "buttonWidth"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (isEnabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(400),
        label = "buttonColor"
    )

    val elevation by animateDpAsState(
        targetValue = if (isEnabled) 12.dp else 4.dp,
        animationSpec = tween(300),
        label = "elevation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!isEnabled) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Completa todos los campos para guardar el contacto",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
            enabled = isEnabled && !isLoading,
            modifier = Modifier
                .height(64.dp)
                .width(animatedWidth)
                .shadow(
                    elevation = elevation,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            AnimatedContent(
                targetState = isLoading,
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                },
                label = "buttonContent"
            ) { loading ->
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 3.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.save),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}