package com.example.contactsapp.presentation.contactlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.contactsapp.R
import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.presentation.components.AlphabetIndex
import com.example.contactsapp.presentation.components.ContactList
import com.example.contactsapp.presentation.components.ContactListSkeleton
import com.example.contactsapp.presentation.components.SimpleSearchField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ContactListScreen(
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
            TopAppBar(
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
                SimpleSearchField(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::updateSearchQuery,
                    onClearClick = viewModel::clearSearch
                )
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
                                ContactList(
                                    contacts = uiState.displayedContacts,
                                    selectedContacts = uiState.selectedContacts,
                                    isSelectionMode = uiState.isSelectionMode,
                                    onContactClick = viewModel::toggleContactSelection,
                                    onContactDelete = viewModel::deleteContactBySwipe,
                                    listState = listState,
                                    modifier = Modifier.weight(1f)
                                )

                                if (uiState.availableLetters.isNotEmpty() && uiState.searchQuery.isEmpty()) {
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
private fun TopAppBar(
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

