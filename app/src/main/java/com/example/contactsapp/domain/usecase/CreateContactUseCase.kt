package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import javax.inject.Inject

class CreateContactUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contact: Contact): Result<Unit> {
        return try {
            when {
                contact.name.isBlank() -> Result.failure(Exception("ERR_NAME"))
                contact.lastName.isBlank() -> Result.failure(Exception("ERR_LASTNAME"))
                contact.phone.isBlank() -> Result.failure(Exception("ERR_PHONE"))
                else -> {
                    repository.insertContact(contact)
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
