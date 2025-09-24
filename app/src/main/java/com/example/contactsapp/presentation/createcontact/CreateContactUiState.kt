package com.example.contactsapp.presentation.createcontact

import com.example.contactsapp.common.StringConstants

data class CreateContactUiState(
    val name: String = StringConstants.EMPTY_STRING,
    val lastName: String = StringConstants.EMPTY_STRING,
    val phone: String = StringConstants.EMPTY_STRING,
    val imageUrl: String = StringConstants.DEFAULT_AVATAR_URL,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val nameError: String? = null,
    val lastNameError: String? = null,
    val phoneError: String? = null,
    val isSaved: Boolean = false,
    val formattedPhone: String = StringConstants.EMPTY_STRING
) {
    val isFormValid: Boolean
        get() = nameError == null && lastNameError == null && phoneError == null &&
            name.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
}
