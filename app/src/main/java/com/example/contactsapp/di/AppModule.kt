package com.example.contactsapp.di

import android.app.Application
import androidx.room.Room
import com.example.contactsapp.data.local.ContactDao
import com.example.contactsapp.data.local.ContactDatabase
import com.example.contactsapp.data.remote.ContactRemoteDataSource
import com.example.contactsapp.data.repository.ContactRepositoryEnhanced
import com.example.contactsapp.domain.repository.ContactRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContactDatabase(app: Application): ContactDatabase {
        return Room.databaseBuilder(
            app,
            ContactDatabase::class.java,
            ContactDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideContactDao(db: ContactDatabase): ContactDao {
        return db.contactDao()
    }

    @Provides
    @Singleton
    fun provideContactRepository(
        dao: ContactDao,
        remoteDataSource: ContactRemoteDataSource
    ): ContactRepository {
        return ContactRepositoryEnhanced(dao, remoteDataSource)
    }
}