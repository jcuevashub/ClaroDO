package com.example.contactsapp.data.repository

import com.example.contactsapp.data.local.ContactDao
import com.example.contactsapp.data.local.toDomain
import com.example.contactsapp.data.local.toEntity
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
) : ContactRepository {

    override fun getContacts(): Flow<List<Contact>> {
        return contactDao.getAllContacts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchContacts(query: String): Flow<List<Contact>> {
        return contactDao.searchContacts("%$query%").map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertContact(contact: Contact) {
        contactDao.insertContact(contact.toEntity())
    }

    override suspend fun deleteContact(contact: Contact) {
        contactDao.deleteContact(contact.toEntity())
    }

    override suspend fun deleteMultipleContacts(contacts: List<Contact>) {
        contactDao.deleteContacts(contacts.map { it.toEntity() })
    }
}