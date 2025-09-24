package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import javax.inject.Inject

class DeleteContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(contacts: List<Contact>): Result<Unit> {
        return try {
            if (contacts.isNotEmpty()) {
                repository.deleteMultipleContacts(contacts)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}