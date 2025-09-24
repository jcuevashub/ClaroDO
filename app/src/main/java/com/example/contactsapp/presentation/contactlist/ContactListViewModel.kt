package com.example.contactsapp.presentation.contactlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.DeleteContactsUseCase
import com.example.contactsapp.domain.usecase.GetContactsUseCase
import com.example.contactsapp.domain.usecase.SearchContactsUseCase
import com.example.contactsapp.domain.usecase.SyncContactsUseCase
import com.example.contactsapp.common.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val getContactsUseCase: GetContactsUseCase,
    private val searchContactsUseCase: SearchContactsUseCase,
    private val deleteContactsUseCase: DeleteContactsUseCase,
    private val syncContactsUseCase: SyncContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactListUiState())
    private val _allContacts = MutableStateFlow<List<Contact>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<ContactListUiState> = combine(
        _uiState,
        _allContacts,
        _searchQuery
    ) { state, allContacts, searchQuery ->
        val filteredContacts = if (searchQuery.isBlank()) {
            allContacts
        } else {
            allContacts.filter { contact ->
                "${contact.name} ${contact.lastName}".contains(searchQuery, ignoreCase = true) ||
                contact.phone.contains(searchQuery)
            }
        }

        val groupedContacts = filteredContacts.groupBy { contact ->
            contact.name.firstOrNull()?.uppercaseChar() ?: '?'
        }
        val availableLetters = groupedContacts.keys.toSet()

        state.copy(
            contacts = filteredContacts,
            searchQuery = searchQuery,
            isSearchActive = searchQuery.isNotBlank(),
            groupedContacts = groupedContacts,
            availableLetters = availableLetters
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContactListUiState()
    )

    private var contactsJob: kotlinx.coroutines.Job? = null

    init {
        loadContacts()
    }

    private fun loadContacts() {
        contactsJob?.cancel()
        contactsJob = viewModelScope.launch {
            _uiState.update { it.copy(showSkeletonLoader = true, error = null) }

            delay(300)

            try {
                getContactsUseCase().collect { contacts ->
                    _allContacts.value = contacts
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showSkeletonLoader = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        showSkeletonLoader = false,
                        error = StringConstants.ERR_UNKNOWN
                    )
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = StringConstants.EMPTY_STRING
    }


    fun toggleContactSelection(contact: Contact) {
        val currentSelection = _uiState.value.selectedContacts
        val newSelection = if (currentSelection.contains(contact)) {
            currentSelection - contact
        } else {
            currentSelection + contact
        }
        
        _uiState.update { 
            it.copy(
                selectedContacts = newSelection,
                isSelectionMode = newSelection.isNotEmpty()
            )
        }
    }

    fun clearSelection() {
        _uiState.update { 
            it.copy(
                selectedContacts = emptySet(),
                isSelectionMode = false
            )
        }
    }

    fun deleteSelectedContacts() {
        val contactsToDelete = _uiState.value.selectedContacts.toList()
        if (contactsToDelete.isNotEmpty()) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                
                deleteContactsUseCase(contactsToDelete)
                    .onSuccess {
                        clearSelection()
                    }
                    .onFailure { _ ->
                        _uiState.update { it.copy(error = StringConstants.ERR_UNKNOWN, isLoading = false) }
                    }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }

            try {
                syncContactsUseCase()
                    .onSuccess {
                        loadContacts()
                    }
                    .onFailure {
                        _uiState.update { it.copy(error = StringConstants.ERR_UNKNOWN) }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = StringConstants.ERR_UNKNOWN) }
            } finally {
                _uiState.update { it.copy(isRefreshing = false) }
            }
        }
    }

    fun scrollToLetter(letter: Char): Int {
        val contacts = _allContacts.value
        return contacts.indexOfFirst { contact ->
            contact.name.firstOrNull()?.uppercaseChar() == letter
        }.takeIf { it >= 0 } ?: 0
    }

    fun deleteContactBySwipe(contact: Contact) {
        viewModelScope.launch {
            deleteContactsUseCase(listOf(contact))
                .onFailure {
                    _uiState.update { it.copy(error = StringConstants.ERR_UNKNOWN) }
                }
        }
    }
}
