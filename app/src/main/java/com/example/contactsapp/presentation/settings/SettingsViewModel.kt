package com.example.contactsapp.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.contactsapp.common.LanguageManager
import com.example.contactsapp.common.LanguageOption
import com.example.contactsapp.domain.usecase.SyncContactsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageManager: LanguageManager,
    private val syncContactsUseCase: SyncContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    val currentLanguage: StateFlow<String> = languageManager.currentLanguage
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "en"
        )

    init {
        _uiState.update {
            it.copy(availableLanguages = languageManager.getAvailableLanguages())
        }
    }

    fun selectLanguage(languageCode: String) {
        viewModelScope.launch {
            languageManager.setLanguage(languageCode)
            _uiState.update {
                it.copy(
                    isLanguageDialogVisible = false,
                    shouldRecreateActivity = true
                )
            }
        }
    }

    fun showLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = true) }
    }

    fun hideLanguageDialog() {
        _uiState.update { it.copy(isLanguageDialogVisible = false) }
    }

    fun syncContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncError = null) }

            syncContactsUseCase()
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            showSyncSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncError = error.message
                        )
                    }
                }
        }
    }

    fun dismissSyncSuccess() {
        _uiState.update { it.copy(showSyncSuccess = false) }
    }

    fun dismissSyncError() {
        _uiState.update { it.copy(syncError = null) }
    }

    fun activityRecreated() {
        _uiState.update { it.copy(shouldRecreateActivity = false) }
    }
}

data class SettingsUiState(
    val availableLanguages: List<LanguageOption> = emptyList(),
    val isLanguageDialogVisible: Boolean = false,
    val shouldRecreateActivity: Boolean = false,
    val isSyncing: Boolean = false,
    val showSyncSuccess: Boolean = false,
    val syncError: String? = null
)