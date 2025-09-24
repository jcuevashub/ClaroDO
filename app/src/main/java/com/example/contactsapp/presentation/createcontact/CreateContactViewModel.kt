package com.example.contactsapp.presentation.createcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.CreateContactUseCase
import com.example.contactsapp.common.StringConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class CreateContactViewModel @Inject constructor(
    private val createContactUseCase: CreateContactUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateContactUiState())
    val uiState: StateFlow<CreateContactUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        val trimmedName = name.trim()
        val err = when {
            trimmedName.isEmpty() && name.isNotEmpty() -> null
            trimmedName.isEmpty() -> ValidationConstants.ERR_NAME_REQUIRED
            trimmedName.length < 2 -> ValidationConstants.ERR_NAME_REQUIRED
            else -> null
        }
        _uiState.update { it.copy(name = name, nameError = err, error = null) }
    }

    fun updateLastName(lastName: String) {
        val trimmedLastName = lastName.trim()
        val err = when {
            trimmedLastName.isEmpty() && lastName.isNotEmpty() -> null
            trimmedLastName.isEmpty() -> ValidationConstants.ERR_LASTNAME_REQUIRED
            trimmedLastName.length < 2 -> ValidationConstants.ERR_LASTNAME_REQUIRED
            else -> null
        }
        _uiState.update { it.copy(lastName = lastName, lastNameError = err, error = null) }
    }

    fun updatePhone(phone: String) {
        val digitsOnly = phone.filter { it.isDigit() }.take(ValidationConstants.PHONE_DIGITS_LENGTH)
        val formattedPhone = formatPhoneNumber(digitsOnly)

        val err = when {
            digitsOnly.isEmpty() -> null
            digitsOnly.length < 7 -> null
            digitsOnly.length == ValidationConstants.PHONE_DIGITS_LENGTH -> {
                val area = digitsOnly.substring(0, 3)
                if (area in ValidationConstants.VALID_AREA_CODES) null
                else ValidationConstants.ERR_AREA_CODE_INVALID
            }
            digitsOnly.length in 7..9 -> ValidationConstants.ERR_PHONE_INVALID
            else -> null
        }

        _uiState.update {
            it.copy(
                phone = digitsOnly,
                formattedPhone = formattedPhone,
                phoneError = err,
                error = null
            )
        }
    }

    private fun formatPhoneNumber(digits: String): String {
        return when (digits.length) {
            0, 1, 2 -> digits
            3 -> digits
            4, 5, 6 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
            7, 8, 9, 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
            else -> digits
        }
    }

    fun refreshImage() {
        val randomId = Random.nextInt(1, 1000)
        val newImageUrl = StringConstants.RANDOM_AVATAR_URL_TEMPLATE + randomId
        _uiState.update { it.copy(imageUrl = newImageUrl) }
    }

    fun saveContact() {
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(error = StringConstants.ERR_FIX_FIELDS) }
            return
        }

        val normalizedPhone = if (state.phone.length == ValidationConstants.PHONE_DIGITS_LENGTH) StringConstants.COUNTRY_CODE_1 + state.phone else state.phone
        val contact = Contact(
            name = state.name.trim(),
            lastName = state.lastName.trim(),
            phone = normalizedPhone,
            imageUrl = state.imageUrl
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, isLoading = true, error = null) }

            createContactUseCase(contact)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaving = false,
                            isSaved = true,
                            error = null
                        )
                    }
                }
                .onFailure { error ->
                    val msg = when (error.message) {
                        StringConstants.ERR_NAME -> StringConstants.ERR_NAME
                        StringConstants.ERR_LASTNAME -> StringConstants.ERR_LASTNAME
                        StringConstants.ERR_PHONE -> StringConstants.ERR_PHONE
                        else -> StringConstants.ERR_UNKNOWN
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSaving = false,
                            error = msg
                        )
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSavedState() {
        _uiState.update { it.copy(isSaved = false) }
    }
}