package com.example.contactsapp.domain.usecase

import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.repository.ContactRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class CreateContactUseCaseTest {

    @Mock
    private lateinit var repository: ContactRepository

    private lateinit var useCase: CreateContactUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCase = CreateContactUseCase(repository)
    }

    @Test
    fun `invoke with valid contact should return success`() = runTest {

        val contact = Contact(
            name = "John",
            lastName = "Doe",
            phone = "123456789",
            imageUrl = "https://example.com/image.jpg"
        )

        val result = useCase(contact)

        assertTrue(result.isSuccess)
        verify(repository).insertContact(contact)
    }

    @Test
    fun `invoke with blank name should return failure`() = runTest {

        val contact = Contact(
            name = "",
            lastName = "Doe",
            phone = "123456789",
            imageUrl = "https://example.com/image.jpg"
        )

        val result = useCase(contact)

        assertTrue(result.isFailure)
        assertEquals("El nombre no puede estar vacío", result.exceptionOrNull()?.message)
        verifyNoInteractions(repository)
    }

    @Test
    fun `invoke with blank lastName should return failure`() = runTest {

        val contact = Contact(
            name = "John",
            lastName = "",
            phone = "123456789",
            imageUrl = "https://example.com/image.jpg"
        )

        val result = useCase(contact)

        assertTrue(result.isFailure)
        assertEquals("El apellido no puede estar vacío", result.exceptionOrNull()?.message)
        verifyNoInteractions(repository)
    }

    @Test
    fun `invoke with blank phone should return failure`() = runTest {

        val contact = Contact(
            name = "John",
            lastName = "Doe",
            phone = "",
            imageUrl = "https://example.com/image.jpg"
        )

        val result = useCase(contact)

        assertTrue(result.isFailure)
        assertEquals("El teléfono no puede estar vacío", result.exceptionOrNull()?.message)
        verifyNoInteractions(repository)
    }

    @Test
    fun `invoke when repository throws exception should return failure`() = runTest {

        val contact = Contact(
            name = "John",
            lastName = "Doe",
            phone = "123456789",
            imageUrl = "https://example.com/image.jpg"
        )
        val exception = RuntimeException("Database error")
        `when`(repository.insertContact(contact)).thenThrow(exception)

        val result = useCase(contact)

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}