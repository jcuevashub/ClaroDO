package com.example.contactsapp.presentation.createcontact

data class CreateContactUiState(
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val imageUrl: String = "https://picsum.photos/200",
    val isLoading: Boolean = false,
    val error: String? = null,
    val nameError: String? = null,
    val lastNameError: String? = null,
    val phoneError: String? = null,
    val isSaved: Boolean = false
) {
    val isFormValid: Boolean
        get() = nameError == null && lastNameError == null && phoneError == null &&
            name.isNotBlank() && lastName.isNotBlank() && phone.isNotBlank()
}
