package com.example.contactsapp.presentation.createcontact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.CreateContactUseCase
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
        val err = if (name.isBlank()) ValidationConstants.ERR_NAME_REQUIRED else null
        _uiState.update { it.copy(name = name, nameError = err, error = null) }
    }

    fun updateLastName(lastName: String) {
        val err = if (lastName.isBlank()) ValidationConstants.ERR_LASTNAME_REQUIRED else null
        _uiState.update { it.copy(lastName = lastName, lastNameError = err, error = null) }
    }

    fun updatePhone(phone: String) {
        val digitsOnly = phone.filter { it.isDigit() }.take(ValidationConstants.PHONE_DIGITS_LENGTH)
        
        val err = when {
            digitsOnly.isEmpty() -> null
            digitsOnly.length < 7 -> null
            digitsOnly.length == ValidationConstants.PHONE_DIGITS_LENGTH -> {
                val area = digitsOnly.substring(0, 3)
                if (area in ValidationConstants.VALID_AREA_CODES) null 
                else ValidationConstants.ERR_AREA_CODE_INVALID
            }
            digitsOnly.length in 7..9 -> ValidationConstants.ERR_PHONE_INVALID // Incomplete
            else -> null
        }

        _uiState.update { it.copy(phone = digitsOnly, phoneError = err, error = null) }
    }

    fun refreshImage() {
        val randomId = Random.nextInt(1, 1000)
        val newImageUrl = "https://picsum.photos/200?random=$randomId"
        _uiState.update { it.copy(imageUrl = newImageUrl) }
    }

    fun saveContact() {
        val state = _uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(error = "ERR_FIX_FIELDS") }
            return
        }

        val normalizedPhone = if (state.phone.length == ValidationConstants.PHONE_DIGITS_LENGTH) "1${state.phone}" else state.phone
        val contact = Contact(
            name = state.name.trim(),
            lastName = state.lastName.trim(),
            phone = normalizedPhone,
            imageUrl = state.imageUrl
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            createContactUseCase(contact)
                .onSuccess {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isSaved = true,
                            error = null
                        ) 
                    }
                }
                .onFailure { error ->
                    val msg = when (error.message) {
                        "ERR_NAME" -> "ERR_NAME"
                        "ERR_LASTNAME" -> "ERR_LASTNAME"
                        "ERR_PHONE" -> "ERR_PHONE"
                        else -> "ERR_UNKNOWN"
                    }
                    _uiState.update { it.copy(isLoading = false, error = msg) }
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
