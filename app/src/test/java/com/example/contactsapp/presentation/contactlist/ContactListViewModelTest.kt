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
        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
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
        // Given
        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))
        
        // When
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.searchQuery)
        assertTrue(state.isSearchActive)
        verify(searchContactsUseCase).invoke(searchQuery)
    }

    @Test
    fun `updateSearchQuery with blank query should set isSearchActive to false`() = runTest {
        // Given - first set a non-blank query
        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - clear the search with blank query
        viewModel.updateSearchQuery("")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
    }

    @Test
    fun `clearSearch should reset search state`() = runTest {
        // Given - first set a search query
        val searchQuery = "John"
        val filteredContacts = listOf(testContacts[0])
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearSearch()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("", state.searchQuery)
        assertFalse(state.isSearchActive)
        // Should show all contacts again (from the original getContactsUseCase mock)
        assertEquals(testContacts.size, state.displayedContacts.size)
    }

    @Test
    fun `toggleContactSelection should add contact to selection`() {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        val contact = testContacts.first()

        // When
        viewModel.toggleContactSelection(contact)

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.selectedContacts.contains(contact))
        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `toggleContactSelection twice should remove contact from selection`() {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        val contact = testContacts.first()
        viewModel.toggleContactSelection(contact) // First selection

        // When
        viewModel.toggleContactSelection(contact) // Second selection (deselect)

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.selectedContacts.contains(contact))
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun `clearSelection should clear all selections`() {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleContactSelection(testContacts[0])
        viewModel.toggleContactSelection(testContacts[1])

        // When
        viewModel.clearSelection()

        // Then
        val state = viewModel.uiState.value
        assertEquals(emptySet<Contact>(), state.selectedContacts)
        assertFalse(state.isSelectionMode)
    }

    @Test
    fun `deleteSelectedContacts should call use case and clear selection on success`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        val contactsToDelete = listOf(testContacts[0], testContacts[1])
        contactsToDelete.forEach { viewModel.toggleContactSelection(it) }
        
        // Mock the suspend function to return success
        runBlocking {
            `when`(deleteContactsUseCase.invoke(contactsToDelete)).thenReturn(Result.success(Unit))
        }

        // When
        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
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
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        val contactsToDelete = listOf(testContacts[0])
        viewModel.toggleContactSelection(contactsToDelete[0])
        
        val errorMessage = "Delete failed"
        runBlocking {
            `when`(deleteContactsUseCase.invoke(contactsToDelete)).thenReturn(Result.failure(Exception(errorMessage)))
        }

        // When
        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals("ERR_UNKNOWN", state.error)
        assertFalse(state.isLoading)
        
        runBlocking {
            verify(deleteContactsUseCase).invoke(contactsToDelete)
        }
    }

    @Test
    fun `deleteSelectedContacts with empty selection should do nothing`() = runTest {
        // When
        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verifyNoInteractions(deleteContactsUseCase)
    }

    @Test
    fun `clearError should set error to null`() = runTest {
        // Given - simulate an error state
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.toggleContactSelection(testContacts[0])
        val selectedContacts = listOf(testContacts[0])
        
        runBlocking {
            `when`(deleteContactsUseCase.invoke(selectedContacts)).thenReturn(Result.failure(Exception("Error")))
        }
        
        viewModel.deleteSelectedContacts()
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearError()

        // Then
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `displayedContacts should filter contacts when searching`() = runTest {
        // Given
        testDispatcher.scheduler.advanceUntilIdle()
        val searchQuery = "John"
        val filteredContacts = listOf(
            testContacts[0], // John Doe
            testContacts[2]  // Bob Johnson
        )
        
        // Mock search use case to return filtered results
        `when`(searchContactsUseCase(searchQuery)).thenReturn(flowOf(filteredContacts))

        // When
        viewModel.updateSearchQuery(searchQuery)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(searchQuery, state.searchQuery)
        assertTrue(state.isSearchActive)
        assertEquals(2, state.displayedContacts.size)
        assertTrue(state.displayedContacts.any { it.name == "John" })
        assertTrue(state.displayedContacts.any { it.lastName == "Johnson" })
        
        // Verify search use case was called
        verify(searchContactsUseCase).invoke(searchQuery)
    }
}