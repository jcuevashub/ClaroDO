package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteContactsUseCaseTest {

    @Mock
    private lateinit var repository: ContactRepository

    private lateinit var useCase: DeleteContactsUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = DeleteContactsUseCase(repository)
    }

    @Test
    fun `invoke with contacts should return success`() = runTest {
        // Given
        val contacts = listOf(
            Contact(1, "John", "Doe", "123", "url1"),
            Contact(2, "Jane", "Smith", "456", "url2")
        )

        // When
        val result = useCase(contacts)

        // Then
        assertTrue(result.isSuccess)
        verify(repository).deleteMultipleContacts(contacts)
    }

    @Test
    fun `invoke with empty list should return success without calling repository`() = runTest {
        // Given
        val contacts = emptyList<Contact>()

        // When
        val result = useCase(contacts)

        // Then
        assertTrue(result.isSuccess)
        verifyNoInteractions(repository)
    }

    @Test
    fun `invoke when repository throws exception should return failure`() = runTest {
        // Given
        val contacts = listOf(
            Contact(1, "John", "Doe", "123", "url1")
        )
        val exception = RuntimeException("Database error")
        `when`(repository.deleteMultipleContacts(contacts)).thenThrow(exception)

        // When
        val result = useCase(contacts)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}