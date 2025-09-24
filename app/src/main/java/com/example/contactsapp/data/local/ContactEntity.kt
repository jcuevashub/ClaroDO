package com.example.contactsapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.contactsapp.common.StringConstants

@Entity(tableName = StringConstants.TABLE_CONTACTS)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val lastName: String,
    val phone: String,
    val imageUrl: String
)