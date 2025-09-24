package com.example.contactsapp.presentation.contactlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.text.style.TextAlign
import com.example.contactsapp.domain.model.Contact
import androidx.compose.ui.res.stringResource
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactListScreen(
    onNavigateToCreateContact: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ContactListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.error) { }
    
    var snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_contacts)) },
                actions = {
                    IconButton(onClick = onNavigateToCreateContact) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_new))
                    }
                    val canDelete = uiState.selectedContacts.isNotEmpty()
                    IconButton(onClick = viewModel::deleteSelectedContacts, enabled = canDelete) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(R.string.action_delete),
                            tint = if (canDelete) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {}
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearch = { },
                active = uiState.isSearchActive,
                onActiveChange = { if (!it) viewModel.clearSearch() },
                placeholder = { Text(stringResource(R.string.search_contacts)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearSearch) {
                            Icon(Icons.Default.Clear, contentDescription = stringResource(R.string.clear_search_desc))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
            }
            
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.displayedContacts.isEmpty() && !uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val emptyText = if (uiState.isSearchActive) stringResource(R.string.empty_search) else stringResource(R.string.empty_list)
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = emptyText,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (uiState.isSearchActive) stringResource(R.string.empty_hint_search) else stringResource(R.string.empty_hint_create),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(16.dp))
                            if (!uiState.isSearchActive) {
                                Button(onClick = onNavigateToCreateContact) {
                                    Icon(Icons.Default.Add, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.add_contact))
                                }
                            } else {
                                OutlinedButton(onClick = { viewModel.clearSearch() }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.clear_search))
                                }
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.displayedContacts) { contact ->
                            ContactItem(
                                contact = contact,
                                isSelected = uiState.selectedContacts.contains(contact),
                                isSelectionMode = uiState.isSelectionMode,
                                onSelectionToggle = { viewModel.toggleContactSelection(contact) }
                            )
                        }
                    }
                }
            }
        }
    }
    
    uiState.error?.let { error ->
        val msg = when (error) {
            StringConstants.ERR_UNKNOWN -> stringResource(R.string.err_unknown)
            else -> error
        }
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message = msg)
            viewModel.clearError()
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onSelectionToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectionToggle() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val hasImage = contact.imageUrl.isNotBlank()
            if (hasImage) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(contact.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.photo_of, contact.name),
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Box(
                            modifier = Modifier.size(56.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val initials = (listOfNotNull(contact.name, contact.lastName)
                                .filter { it.isNotBlank() }
                                .map { it.trim().firstOrNull()?.uppercase() ?: StringConstants.EMPTY_STRING }
                                .take(2)
                                .joinToString(StringConstants.EMPTY_STRING)
                                ).ifBlank { stringResource(R.string.contact_initial_placeholder) }
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = contact.name + StringConstants.SPACE + contact.lastName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                val countryCode = stringResource(R.string.country_code)
                val formattedPhone = remember(contact.phone, countryCode) {
                    val digits = contact.phone.filter { it.isDigit() }
                    if (digits.length >= 2) {
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
                Text(
                    text = formattedPhone,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelectionMode) {
                if (isSelected) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.not_selected),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
