package com.example.contactsapp.data.repository

import com.example.contactsapp.data.local.ContactDao
import com.example.contactsapp.data.local.toDomain
import com.example.contactsapp.data.local.toEntity
import com.example.contactsapp.data.remote.ContactRemoteDataSource
import com.example.contactsapp.data.remote.NetworkResult
import com.example.contactsapp.data.remote.toDomain
import com.example.contactsapp.data.remote.toEntity
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import com.example.contactsapp.common.StringConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryEnhanced @Inject constructor(
    private val contactDao: ContactDao,
    private val remoteDataSource: ContactRemoteDataSource
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        return flow {
            // First emit cached data
            contactDao.getAllContacts().collect { localContacts ->
                emit(localContacts.map { it.toDomain() })
            }

            // Then try to sync with remote
            syncContactsFromRemote()
        }
    }

    override fun searchContacts(query: String): Flow<List<Contact>> {
        return flow {
            // First emit local search results
            contactDao.searchContacts(StringConstants.PERCENT_WRAPPER + query + StringConstants.PERCENT_WRAPPER)
                .collect { localContacts ->
                    emit(localContacts.map { it.toDomain() })
                }

            // Then try remote search (optional)
            searchContactsFromRemote(query)
        }
    }

    override suspend fun insertContact(contact: Contact) {
        // Try remote first
        when (val result = remoteDataSource.createContact(contact)) {
            is NetworkResult.Success -> {
                // Save remote result to local DB
                contactDao.insertContact(result.data.toEntity())
            }
            is NetworkResult.Error, is NetworkResult.Exception -> {
                // Fallback to local only
                contactDao.insertContact(contact.toEntity())
                // TODO: Add to sync queue for later retry
            }
        }
    }

    override suspend fun deleteContact(contact: Contact) {
        // Try remote first
        when (val result = remoteDataSource.deleteContact(contact.id)) {
            is NetworkResult.Success -> {
                // Remove from local DB
                contactDao.deleteContact(contact.toEntity())
            }
            is NetworkResult.Error, is NetworkResult.Exception -> {
                // Fallback to local only
                contactDao.deleteContact(contact.toEntity())
                // TODO: Add to sync queue for later retry
            }
        }
    }

    override suspend fun deleteMultipleContacts(contacts: List<Contact>) {
        // Try remote first
        when (val result = remoteDataSource.deleteContacts(contacts)) {
            is NetworkResult.Success -> {
                // Remove from local DB
                contactDao.deleteContacts(contacts.map { it.toEntity() })
            }
            is NetworkResult.Error, is NetworkResult.Exception -> {
                // Fallback to local only
                contactDao.deleteContacts(contacts.map { it.toEntity() })
                // TODO: Add to sync queue for later retry
            }
        }
    }

    suspend fun updateContact(contact: Contact): Result<Unit> {
        return try {
            when (val result = remoteDataSource.updateContact(contact)) {
                is NetworkResult.Success -> {
                    contactDao.insertContact(result.data.toEntity())
                    Result.success(Unit)
                }
                is NetworkResult.Error -> {
                    Result.failure(Exception(remoteDataSource.handleNetworkError(result)))
                }
                is NetworkResult.Exception -> {
                    Result.failure(result.throwable)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forceSync(): Result<Unit> {
        return try {
            syncContactsFromRemote()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun syncContactsFromRemote() {
        when (val result = remoteDataSource.getContacts()) {
            is NetworkResult.Success -> {
                // Clear local data and insert remote data
                // Note: In a real app, you might want more sophisticated sync logic
                result.data.forEach { remoteContact ->
                    contactDao.insertContact(remoteContact.toEntity())
                }
            }
            is NetworkResult.Error, is NetworkResult.Exception -> {
                // Keep local data, no sync
                // TODO: Log error or notify user about sync issues
            }
        }
    }

    private suspend fun searchContactsFromRemote(query: String) {
        when (val result = remoteDataSource.searchContacts(query)) {
            is NetworkResult.Success -> {
                // Update local cache with search results
                result.data.forEach { remoteContact ->
                    contactDao.insertContact(remoteContact.toEntity())
                }
            }
            is NetworkResult.Error, is NetworkResult.Exception -> {
                // Use only local results
                // TODO: Handle search sync errors
            }
        }
    }
}