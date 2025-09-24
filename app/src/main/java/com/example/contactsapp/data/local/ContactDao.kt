package com.example.contactsapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.contactsapp.common.StringConstants

@Dao
interface ContactDao {
    @Query(StringConstants.QUERY_ALL_CONTACTS)
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Query(StringConstants.QUERY_SEARCH_CONTACTS)
    fun searchContacts(query: String): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContacts(contacts: List<ContactEntity>)
}