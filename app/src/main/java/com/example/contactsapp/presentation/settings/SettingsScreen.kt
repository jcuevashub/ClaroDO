package com.example.contactsapp.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.R
import com.example.contactsapp.common.LanguageOption
import com.example.contactsapp.common.StringConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLanguageChanged: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle activity recreation when language changes
    LaunchedEffect(uiState.shouldRecreateActivity) {
        if (uiState.shouldRecreateActivity) {
            onLanguageChanged()
            viewModel.activityRecreated()
        }
    }

    // Show sync success message
    LaunchedEffect(uiState.showSyncSuccess) {
        if (uiState.showSyncSuccess) {
            snackbarHostState.showSnackbar(
                message = "Contacts synced successfully" // This should use stringResource in real implementation
            )
            viewModel.dismissSyncSuccess()
        }
    }

    // Show sync error message
    LaunchedEffect(uiState.syncError) {
        uiState.syncError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error
            )
            viewModel.dismissSyncError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Setting
            SettingItem(
                icon = Icons.Default.Person,
                title = stringResource(R.string.language),
                subtitle = getCurrentLanguageName(currentLanguage, uiState.availableLanguages),
                onClick = { viewModel.showLanguageDialog() }
            )

            HorizontalDivider()

            // Sync Setting
            SettingItem(
                icon = Icons.Default.Add,
                title = stringResource(R.string.action_sync),
                subtitle = if (uiState.isSyncing)
                    stringResource(R.string.syncing)
                else
                    "Sync contacts with server",
                onClick = { viewModel.syncContacts() },
                showLoading = uiState.isSyncing
            )
        }
    }

    // Language Selection Dialog
    if (uiState.isLanguageDialogVisible) {
        LanguageSelectionDialog(
            languages = uiState.availableLanguages,
            currentLanguage = currentLanguage,
            onLanguageSelected = { viewModel.selectLanguage(it) },
            onDismiss = { viewModel.hideLanguageDialog() }
        )
    }
}

@Composable
private fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    showLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!showLoading) onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    languages: List<LanguageOption>,
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.select_language)) },
        text = {
            Column {
                languages.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language.code) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RadioButton(
                            selected = language.code == currentLanguage,
                            onClick = { onLanguageSelected(language.code) }
                        )
                        Column {
                            Text(
                                text = language.nativeName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = language.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun getCurrentLanguageName(
    currentLanguage: String,
    availableLanguages: List<LanguageOption>
): String {
    return availableLanguages.find { it.code == currentLanguage }?.nativeName
        ?: stringResource(R.string.language_english)
}