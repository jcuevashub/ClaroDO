package com.example.contactsapp.data.remote

import com.example.contactsapp.common.StringConstants
import com.example.contactsapp.common.StringResources
import com.example.contactsapp.domain.model.Contact
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRemoteDataSource @Inject constructor(
    private val apiService: ContactApiService,
    private val stringResources: StringResources
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
                    400 -> stringResources.getApiErrorBadRequest()
                    401 -> stringResources.getApiErrorUnauthorized()
                    403 -> stringResources.getApiErrorForbidden()
                    404 -> stringResources.getApiErrorNotFound()
                    500 -> stringResources.getApiErrorServerError()
                    else -> networkResult.message
                }
            }
            is NetworkResult.Exception -> {
                when (networkResult.throwable) {
                    is java.net.UnknownHostException -> stringResources.getApiErrorNoInternet()
                    is java.net.SocketTimeoutException -> stringResources.getApiErrorTimeout()
                    else -> stringResources.getApiErrorUnknown()
                }
            }
            else -> stringResources.getApiErrorUnknown()
        }
    }
}