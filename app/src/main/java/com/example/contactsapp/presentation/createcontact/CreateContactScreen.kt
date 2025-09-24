package com.example.contactsapp.presentation.createcontact

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.stringResource
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateContactScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateContactViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.new_contact)) },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) { Text(stringResource(R.string.cancel)) }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { viewModel.refreshImage() },
                    shape = CircleShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    if (uiState.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uiState.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.photo_of, uiState.name.ifBlank { StringConstants.EMPTY_STRING }),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                val initials = listOf(uiState.name, uiState.lastName)
                                    .filter { it.isNotBlank() }
                                    .map { it.trim().firstOrNull()?.uppercase() ?: StringConstants.EMPTY_STRING }
                                    .take(2)
                                    .joinToString(StringConstants.EMPTY_STRING)
                                    .ifBlank { stringResource(R.string.contact_initial_placeholder) }
                                Text(
                                    text = initials,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.tap_image_change),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::updateName,
                    label = { Text(stringResource(R.string.name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.nameError != null,
                    supportingText = { uiState.nameError?.let { Text(stringResource(R.string.err_name_required)) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.lastName,
                    onValueChange = viewModel::updateLastName,
                    label = { Text(stringResource(R.string.last_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.lastNameError != null,
                    supportingText = { uiState.lastNameError?.let { Text(stringResource(R.string.err_lastname_required)) } }
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.phone,
                    onValueChange = { input ->
                        val digits = input.filter { it.isDigit() }.take(10)
                        viewModel.updatePhone(digits)
                    },
                    label = { Text(stringResource(R.string.phone)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    visualTransformation = PhoneVisualTransformation(),
                    isError = uiState.phoneError != null,
                    supportingText = {
                        uiState.phoneError?.let {
                            val isArea = it.contains(stringResource(R.string.contact_area_code))
                            Text(stringResource(if (isArea) R.string.err_phone_area else R.string.err_phone_invalid))
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = viewModel::saveContact,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading && uiState.isFormValid
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(text = if (uiState.isLoading) stringResource(R.string.saving) else stringResource(R.string.save))
                }

                uiState.error?.let { error ->
                    val msg = when (error) {
                        StringConstants.ERR_FIX_FIELDS -> stringResource(R.string.err_fix_fields)
                        StringConstants.ERR_NAME -> stringResource(R.string.err_name_required)
                        StringConstants.ERR_LASTNAME -> stringResource(R.string.err_lastname_required)
                        StringConstants.ERR_PHONE -> stringResource(R.string.err_phone_invalid)
                        StringConstants.ERR_UNKNOWN -> stringResource(R.string.err_save_contact)
                        else -> error
                    }
                    LaunchedEffect(error) { snackbarHostState.showSnackbar(msg) }
                }
            }
        }
    }
}