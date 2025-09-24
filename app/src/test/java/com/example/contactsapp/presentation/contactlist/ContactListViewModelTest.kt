package com.example.contactsapp.presentation.contactlist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.contactsapp.domain.model.Contact
import com.example.contactsapp.domain.usecase.DeleteContactsUseCase
import com.example.contactsapp.domain.usecase.GetContactsUseCase
import com.example.contactsapp.domain.usecase.SearchContactsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.anyList
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ContactListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var getContactsUseCase: GetContactsUseCase

    @Mock
    private lateinit var searchContactsUseCase: SearchContactsUseCase

    @Mock
    private lateinit var deleteContactsUseCase: DeleteContactsUseCase

    private lateinit var viewModel: ContactListViewModel

    private val testContacts = listOf(
        Contact(1, "John", "Doe", "123456789", "url1"),
        Contact(2, "Jane", "Smith", "987654321", "url2"),
        Contact(3, "Bob", "Johnson", "555666777", "url3")
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(getContactsUseCase()).thenReturn(flowOf(testContacts))

        viewModel = ContactListViewModel(
            getContactsUseCase,
            searchContactsUseCase,
            deleteContactsUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should load contacts`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(testContacts, state.contacts)
        assertEquals(testContacts, state.displayedContacts)
        assertFalse(state.isLoading)
        assertEquals(null, state.error)
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
        assertEquals(emptySet<Contact>(), state.selectedContacts)
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun `updateSearchQuery should update search state`() = runTest {

        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))

        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.searchQuery)
        assertTrue(state.isSearchActive)
        verify(searchContactsUseCase).invoke(searchQuery)
    }

    @Test
    fun `updateSearchQuery with blank query should set isSearchActive to false`() = runTest {

        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
    }

    @Test
    fun `clearSearch should reset search state`() = runTest {

        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)

        assertEquals(testContacts.size, state.displayedContacts.size)
    }

    @Test
    fun `toggleContactSelection should add contact to selection`() {

        testDispatcher.scheduler.advanceUntilIdle()
        val contact = testContacts.first()

        viewModel.toggleContactSelection(contact)

        val state = viewModel.uiState.value
        assertTrue(state.selectedContacts.contains(contact))
        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `toggleContactSelection twice should remove contact from selection`() {

        testDispatcher.scheduler.advanceUntilIdle()
        val contact = testContacts.first()
        viewModel.toggleContactSelection(contact)

        viewModel.toggleContactSelection(contact)

        val state = viewModel.uiState.value
        assertFalse(state.selectedContacts.contains(contact))
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun `clearSelection should clear all selections`() {

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleContactSelection(testContacts[0])
        viewModel.toggleContactSelection(testContacts[1])

        viewModel.clearSelection()

        val state = viewModel.uiState.value
        assertEquals(emptySet<Contact>(), state.selectedContacts)
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun `deleteSelectedContacts should call use case and clear selection on success`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()
        val contactsToDelete = listOf(testContacts[0], testContacts[1])
        contactsToDelete.forEach { viewModel.toggleContactSelection(it) }

        runBlocking {
            `when`(deleteContactsUseCase.invoke(contactsToDelete)).thenReturn(Result.success(Unit))
        }

        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(emptySet<Contact>(), state.selectedContacts)
        assertFalse(state.isSelectionMode)
        assertEquals(null, state.error)

        runBlocking {
            verify(deleteContactsUseCase).invoke(contactsToDelete)
        }
    }

    @Test
    fun `deleteSelectedContacts should set error on failure`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()
        val contactsToDelete = listOf(testContacts[0])
        viewModel.toggleContactSelection(contactsToDelete[0])

        val errorMessage = "Delete failed"
        runBlocking {
            `when`(deleteContactsUseCase.invoke(contactsToDelete)).thenReturn(Result.failure(Exception(errorMessage)))
        }

        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("ERR_UNKNOWN", state.error)
        assertFalse(state.isLoading)

        runBlocking {
            verify(deleteContactsUseCase).invoke(contactsToDelete)
        }
    }

    @Test
    fun `deleteSelectedContacts with empty selection should do nothing`() = runTest {

        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        verifyNoInteractions(deleteContactsUseCase)
    }

    @Test
    fun `clearError should set error to null`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleContactSelection(testContacts[0])
        val selectedContacts = listOf(testContacts[0])

        runBlocking {
            `when`(deleteContactsUseCase.invoke(selectedContacts)).thenReturn(Result.failure(Exception("Error")))
        }

        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.clearError()

        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `displayedContacts should filter contacts when searching`() = runTest {

        testDispatcher.scheduler.advanceUntilIdle()
        val searchQuery = "John"
        val filteredContacts = listOf(
            testContacts[0],
            testContacts[2]
        )

        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))

        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.searchQuery)
        assertTrue(state.isSearchActive)
        assertEquals(2, state.displayedContacts.size)
        assertTrue(state.displayedContacts.any { it.name == "John" })
        assertTrue(state.displayedContacts.any { it.lastName == "Johnson" })

        verify(searchContactsUseCase).invoke(searchQuery)
    }
}