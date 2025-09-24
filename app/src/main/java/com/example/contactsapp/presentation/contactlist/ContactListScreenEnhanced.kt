package com.example.contactsapp.presentation.contactlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.presentation.components.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreenEnhanced(
    onNavigateToCreateContact: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ContactListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = LocalHapticFeedback.current
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            EnhancedTopAppBar(
                selectedCount = uiState.selectedCount,
                isSelectionMode = uiState.isSelectionMode,
                onSettingsClick = onNavigateToSettings,
                onAddClick = onNavigateToCreateContact,
                onDeleteClick = viewModel::deleteSelectedContacts,
                onClearSelection = viewModel::clearSelection
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(modifier = modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                var searchActive by remember { mutableStateOf(false) }

                val onActiveChange: (Boolean) -> Unit = { isActive ->
                    searchActive = isActive
                    if (!isActive) {
                        viewModel.clearSearch()
                    }
                }

                EnhancedSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    searchActive = searchActive,
                    onActiveChange = onActiveChange,
                    searchResults = uiState.displayedContacts.take(5),
                    onResultClick = { contact ->
                        searchActive = false
                        viewModel.clearSearch()
                    }
                )

                if (!searchActive) {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = viewModel::refresh,
                        state = pullToRefreshState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            when {
                                uiState.showSkeletonLoader -> {
                                    ContactListSkeleton(
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                uiState.displayedContacts.isEmpty() && !uiState.isLoading -> {
                                    EnhancedEmptyState(
                                        isSearchActive = uiState.isSearchActive,
                                        onCreateContact = onNavigateToCreateContact,
                                        onClearSearch = viewModel::clearSearch,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                else -> {
                                    EnhancedContactList(
                                        contacts = uiState.displayedContacts,
                                        selectedContacts = uiState.selectedContacts,
                                        isSelectionMode = uiState.isSelectionMode,
                                        onContactClick = viewModel::toggleContactSelection,
                                        onContactDelete = viewModel::deleteContactBySwipe,
                                        listState = listState,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (uiState.availableLetters.isNotEmpty()) {
                                        AlphabetIndex(
                                            availableLetters = uiState.availableLetters,
                                            onLetterClick = { letter ->
                                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                                val index = viewModel.scrollToLetter(letter)
                                                coroutineScope.launch {
                                                    listState.animateScrollToItem(index)
                                                }
                                            },
                                            modifier = Modifier
                                                .fillMaxHeight()
                                                .padding(end = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    EnhancedSearchSuggestions(
                        query = uiState.searchQuery,
                        modifier = Modifier.fillMaxSize()
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedTopAppBar(
    selectedCount: Int,
    isSelectionMode: Boolean,
    onSettingsClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onClearSelection: () -> Unit
) {
    AnimatedTopAppBar(
        title = if (isSelectionMode) {
            stringResource(R.string.selection_count, selectedCount)
        } else {
            stringResource(R.string.title_contacts)
        },
        isSelectionMode = isSelectionMode,
        navigationIcon = if (isSelectionMode) {
            {
                IconButton(onClick = onClearSelection) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear selection")
                }
            }
        } else null,
        actions = {
            if (!isSelectionMode) {
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                }
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_new))
                }
            } else {
                IconButton(
                    onClick = onDeleteClick,
                    enabled = selectedCount > 0
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = if (selectedCount > 0) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AnimatedTopAppBar(
    title: String,
    isSelectionMode: Boolean,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelectionMode) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        animationSpec = tween(300),
        label = "topBarColor"
    )

    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = if (isSelectionMode) FontWeight.Bold else FontWeight.Normal
            )
        },
        navigationIcon = navigationIcon ?: {},
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = animatedColor
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    searchActive: Boolean,
    onActiveChange: (Boolean) -> Unit,
    searchResults: List<Contact>,
    onResultClick: (Contact) -> Unit,
    modifier: Modifier = Modifier
) {
    DockedSearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = searchActive,
                onExpandedChange = onActiveChange,
                placeholder = { Text(stringResource(R.string.search_contacts)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            onQueryChange("")
                            onActiveChange(false)
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = stringResource(R.string.clear_search_desc)
                            )
                        }
                    }
                }
            )
        },
        expanded = searchActive,
        onExpandedChange = onActiveChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        content = {
            if (query.isNotEmpty() && searchResults.isNotEmpty()) {
                LazyColumn {
                    items(searchResults) { contact ->
                        SearchResultItem(
                            contact = contact,
                            onClick = { onResultClick(contact) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun SearchResultItem(
    contact: Contact,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text("${contact.name} ${contact.lastName}")
        },
        supportingContent = {
            Text(contact.phone)
        },
        leadingContent = {
            DynamicAvatar(
                name = contact.name,
                lastName = contact.lastName,
                imageUrl = contact.imageUrl,
                size = 40.dp
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

@Composable
private fun EnhancedEmptyState(
    isSearchActive: Boolean,
    onCreateContact: () -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val emptyText = if (isSearchActive) stringResource(R.string.empty_search)
            else stringResource(R.string.empty_list)

            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(72.dp)
            )

            Text(
                text = emptyText,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = if (isSearchActive) stringResource(R.string.empty_hint_search)
                else stringResource(R.string.empty_hint_create),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!isSearchActive) {
                Button(onClick = onCreateContact) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.add_contact))
                }
            } else {
                OutlinedButton(onClick = onClearSearch) {
                    Icon(Icons.Default.Clear, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.clear_search))
                }
            }
        }
    }
}

@Composable
private fun EnhancedSearchSuggestions(
    query: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (query.isEmpty()) stringResource(R.string.search_contacts)
            else stringResource(R.string.searching, query),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (query.isEmpty()) stringResource(R.string.search_hint)
            else stringResource(R.string.search_results_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}