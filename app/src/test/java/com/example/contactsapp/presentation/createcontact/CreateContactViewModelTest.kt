package com.example.contactsapp.presentation.createcontact

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.contactsapp.domain.usecase.CreateContactUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateContactViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var createContactUseCase: CreateContactUseCase
    private lateinit var viewModel: CreateContactViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        createContactUseCase = mock()
        viewModel = CreateContactViewModel(createContactUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state defaults`() {
        val s = viewModel.uiState.value
        assertEquals("", s.name)
        assertEquals("", s.lastName)
        assertEquals("", s.phone)
        assertEquals("https://picsum.photos/200", s.imageUrl)
        assertFalse(s.isLoading)
        assertFalse(s.isSaved)
        assertFalse(s.isFormValid)
    }

    @Test
    fun `name and lastName validation errors`() {
        viewModel.updateName("")
        viewModel.updateLastName("")
        assertEquals("El nombre es obligatorio", viewModel.uiState.value.nameError)
        assertEquals("El apellido es obligatorio", viewModel.uiState.value.lastNameError)

        viewModel.updateName("Ana")
        viewModel.updateLastName("García")
        assertEquals(null, viewModel.uiState.value.nameError)
        assertEquals(null, viewModel.uiState.value.lastNameError)
    }

    @Test
    fun `phone formatting RD and normalization`() {

        viewModel.updatePhone("8095551234")
        assertEquals("+1 809-555-1234", viewModel.uiState.value.phone)
        assertEquals(null, viewModel.uiState.value.phoneError)

        viewModel.updatePhone("0005551234")
        assertEquals(true, viewModel.uiState.value.phoneError?.contains("Código de área") == true)
    }

    @Test
    fun `saveContact invalid shows general error and not call usecase`() = runTest {
        viewModel.saveContact()
        assertEquals("Revisa los campos marcados en rojo", viewModel.uiState.value.error)
        Mockito.verifyNoInteractions(createContactUseCase)
    }

    @Test
    fun `saveContact success normalizes digits`() = runTest {
        viewModel.updateName("Ana")
        viewModel.updateLastName("García")
        viewModel.updatePhone("+1 809-555-1234")

        Mockito.`when`(createContactUseCase.invoke(any())).thenReturn(Result.success(Unit))

        viewModel.saveContact()
        advanceUntilIdle()

        val s = viewModel.uiState.value
        assertTrue(s.isSaved)
        assertFalse(s.isLoading)
        assertEquals(null, s.error)
    }
}