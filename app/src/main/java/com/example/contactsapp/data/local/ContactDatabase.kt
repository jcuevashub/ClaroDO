package com.example.contactsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.contactsapp.common.StringConstants

@Database(
    entities = [ContactEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        const val DATABASE_NAME = StringConstants.DATABASE_NAME
    }
}
