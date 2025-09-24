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
    val uiState: StateFlow<ContactListUiState> = _uiState.asStateFlow()
    
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
                    val groupedContacts = contacts.groupBy { contact ->
                        contact.name.firstOrNull()?.uppercaseChar() ?: '?'
                    }
                    val availableLetters = groupedContacts.keys.toSet()

                    _uiState.update {
                        it.copy(
                            contacts = contacts,
                            groupedContacts = groupedContacts,
                            availableLetters = availableLetters,
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
        _uiState.update { 
            it.copy(
                searchQuery = query,
                isSearchActive = query.isNotBlank()
            )
        }
        
        performSearch(query)
    }
    
    private fun performSearch(query: String) {
        contactsJob?.cancel()
        contactsJob = viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    getContactsUseCase().collect { contacts ->
                        _uiState.update { it.copy(contacts = contacts) }
                    }
                } else {
                    searchContactsUseCase(query).collect { contacts ->
                        _uiState.update { it.copy(contacts = contacts) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = StringConstants.ERR_UNKNOWN) }
            }
        }
    }

    fun clearSearch() {
        _uiState.update { 
            it.copy(
                searchQuery = StringConstants.EMPTY_STRING,
                isSearchActive = false
            )
        }
        performSearch(StringConstants.EMPTY_STRING)
    }

    fun setSearchActive(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
        if (!active && _uiState.value.searchQuery.isBlank()) {
            performSearch(StringConstants.EMPTY_STRING)
        }
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
        val contacts = _uiState.value.contacts
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
