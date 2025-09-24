package com.example.contactsapp.presentation.createcontact

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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

    Scaffold(
        topBar = {
            EnhancedTopBar(
                onNavigateBack = onNavigateBack,
                onSave = {
                    keyboardController?.hide()
                    viewModel.saveContact()
                },
                isSaving = uiState.isSaving,
                isFormValid = uiState.name.isNotBlank() &&
                             uiState.lastName.isNotBlank() &&
                             uiState.phone.isNotBlank()
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            EnhancedImageSelector(
                imageUrl = uiState.imageUrl,
                name = uiState.name,
                lastName = uiState.lastName,
                onRefreshImage = viewModel::refreshImage
            )

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(32.dp))

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

            Spacer(modifier = Modifier.height(16.dp))
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
    val interactionSource = remember { MutableInteractionSource() }
    val animatedScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "imageScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier
                .size(120.dp)
                .scale(animatedScale)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onRefreshImage() },
            shape = CircleShape,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                        size = 120.dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.secondaryContainer
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        Text(
            text = stringResource(R.string.tap_image_change),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ContactFormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            content = content
        )
    }
}

@Composable
private fun EnhancedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    errorText: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() },
    supportingText: String? = null,
    placeholder: String? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = when {
            errorText != null -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        },
        label = "borderColor"
    )

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = if (placeholder != null) {
                { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) }
            } else null,
            leadingIcon = {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = if (isFocused) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { isFocused = it.isFocused },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = true,
            isError = errorText != null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = borderColor,
                unfocusedBorderColor = borderColor,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp)
        )

        AnimatedVisibility(
            visible = errorText != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Text(
                text = errorText ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        if (supportingText != null && errorText == null) {
            Text(
                text = supportingText,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
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
    val animatedWidth by animateDpAsState(
        targetValue = if (isLoading) 56.dp else 200.dp,
        animationSpec = tween(300),
        label = "buttonWidth"
    )

    val buttonColor by animateColorAsState(
        targetValue = if (isEnabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        label = "buttonColor"
    )

    Button(
        onClick = onClick,
        enabled = isEnabled && !isLoading,
        modifier = Modifier
            .height(56.dp)
            .width(animatedWidth),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(28.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 3.dp
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.save),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}