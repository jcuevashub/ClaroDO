package com.example.contactsapp.domain.usecase

import com.example.contactsapp.data.repository.ContactRepositoryEnhanced
import com.example.contactsapp.domain.repository.ContactRepository
import javax.inject.Inject

class SyncContactsUseCase @Inject constructor(
    private val repository: ContactRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return if (repository is ContactRepositoryEnhanced) {
            repository.forceSync()
        } else {
            Result.success(Unit) // No-op for local-only repository
        }
    }
}