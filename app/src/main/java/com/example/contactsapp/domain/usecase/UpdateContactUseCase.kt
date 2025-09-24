package com.example.contactsapp.domain.usecase

import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.data.repository.ContactRepositoryEnhanced
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import javax.inject.Inject

class UpdateContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact): Result<Unit> {
        return try {
            when {
                contact.name.isBlank() -> Result.failure(Exception(StringConstants.ERR_NAME))
                contact.lastName.isBlank() -> Result.failure(Exception(StringConstants.ERR_LASTNAME))
                contact.phone.isBlank() -> Result.failure(Exception(StringConstants.ERR_PHONE))
                else -> {
                    if (repository is ContactRepositoryEnhanced) {
                        repository.updateContact(contact)
                    } else {
                        // Fallback for basic repository - treat as insert
                        repository.insertContact(contact)
                        Result.success(Unit)
                    }
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}