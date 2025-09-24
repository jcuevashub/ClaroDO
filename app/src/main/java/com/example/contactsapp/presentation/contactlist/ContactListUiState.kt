package com.example.contactsapp.presentation.contactlist

import com.example.contactsapp.domain.model.Contact

data class ContactListUiState(
    val contacts: List<Contact> = emptyList(),
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedContacts: Set<Contact> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSelectionMode: Boolean = false
) {
    // Since we're doing database-level search, displayedContacts is just contacts
    val displayedContacts: List<Contact> get() = contacts
}