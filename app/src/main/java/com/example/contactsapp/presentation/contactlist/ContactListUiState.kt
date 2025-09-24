package com.example.contactsapp.presentation.contactlist

import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.common.StringConstants

data class ContactListUiState(
    val contacts: List<Contact> = emptyList(),
    val searchQuery: String = StringConstants.EMPTY_STRING,
    val isSearchActive: Boolean = false,
    val selectedContacts: Set<Contact> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSelectionMode: Boolean = false
) {
    // Since we're doing database-level search, displayedContacts is just contacts
    val displayedContacts: List<Contact> get() = contacts
}