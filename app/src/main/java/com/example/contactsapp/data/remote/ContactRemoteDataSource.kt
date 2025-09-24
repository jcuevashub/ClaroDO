package com.example.contactsapp.data.remote

import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.domain.model.Contact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRemoteDataSource @Inject constructor(
    private val apiService: ContactApiService
) {

    suspend fun getContacts(): NetworkResult<List<ContactRemoteDto>> {
        return safeApiCall { apiService.getContacts() }
    }

    suspend fun searchContacts(query: String): NetworkResult<List<ContactRemoteDto>> {
        return safeApiCall { apiService.searchContacts(query) }
    }

    suspend fun createContact(contact: Contact): NetworkResult<ContactRemoteDto> {
        return safeApiCall {
            apiService.createContact(contact.toCreateRequest())
        }
    }

    suspend fun updateContact(contact: Contact): NetworkResult<ContactRemoteDto> {
        return safeApiCall {
            apiService.updateContact(contact.id, contact.toUpdateRequest())
        }
    }

    suspend fun deleteContact(contactId: Long): NetworkResult<Unit> {
        return safeApiCall { apiService.deleteContact(contactId) }
    }

    suspend fun deleteContacts(contacts: List<Contact>): NetworkResult<Unit> {
        return safeApiCall {
            apiService.deleteContacts(contacts.toBatchDeleteRequest())
        }
    }

    fun handleNetworkError(networkResult: NetworkResult<*>): String {
        return when (networkResult) {
            is NetworkResult.Error -> {
                when (networkResult.code) {
                    400 -> StringConstants.API_ERROR_BAD_REQUEST
                    401 -> StringConstants.API_ERROR_UNAUTHORIZED
                    403 -> StringConstants.API_ERROR_FORBIDDEN
                    404 -> StringConstants.API_ERROR_NOT_FOUND
                    500 -> StringConstants.API_ERROR_SERVER_ERROR
                    else -> networkResult.message
                }
            }
            is NetworkResult.Exception -> {
                when (networkResult.throwable) {
                    is java.net.UnknownHostException -> StringConstants.API_ERROR_NO_INTERNET
                    is java.net.SocketTimeoutException -> StringConstants.API_ERROR_TIMEOUT
                    else -> StringConstants.API_ERROR_UNKNOWN
                }
            }
            else -> StringConstants.API_ERROR_UNKNOWN
        }
    }
}