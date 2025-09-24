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
    val isSelectionMode: Boolean = false,
    val isRefreshing: Boolean = false,
    val groupedContacts: Map<Char, List<Contact>> = emptyMap(),
    val availableLetters: Set<Char> = emptySet(),
    val showSkeletonLoader: Boolean = false
) {
    val displayedContacts: List<Contact> get() = contacts
    val selectedCount: Int get() = selectedContacts.size
}