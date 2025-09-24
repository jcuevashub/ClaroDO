package com.example.contactsapp.data.remote

import com.example.contactsapp.common.StringConstants
import retrofit2.Response
import retrofit2.http.*

interface ContactApiService {

    @GET(StringConstants.API_CONTACTS_ENDPOINT)
    suspend fun getContacts(): Response<List<ContactRemoteDto>>

    @GET(StringConstants.API_CONTACTS_ENDPOINT)
    suspend fun searchContacts(
        @Query(StringConstants.API_QUERY_PARAM) query: String
    ): Response<List<ContactRemoteDto>>

    @POST(StringConstants.API_CONTACTS_ENDPOINT)
    suspend fun createContact(
        @Body contact: ContactCreateRequest
    ): Response<ContactRemoteDto>

    @PUT("${StringConstants.API_CONTACTS_ENDPOINT}/{${StringConstants.API_ID_PARAM}}")
    suspend fun updateContact(
        @Path(StringConstants.API_ID_PARAM) id: Long,
        @Body contact: ContactUpdateRequest
    ): Response<ContactRemoteDto>

    @DELETE("${StringConstants.API_CONTACTS_ENDPOINT}/{${StringConstants.API_ID_PARAM}}")
    suspend fun deleteContact(
        @Path(StringConstants.API_ID_PARAM) id: Long
    ): Response<Unit>

    @POST(StringConstants.API_CONTACTS_BATCH_DELETE_ENDPOINT)
    suspend fun deleteContacts(
        @Body request: ContactBatchDeleteRequest
    ): Response<Unit>
}

@kotlinx.serialization.Serializable
data class ContactCreateRequest(
    val name: String,
    @kotlinx.serialization.SerialName("last_name")
    val lastName: String,
    val phone: String,
    @kotlinx.serialization.SerialName("image_url")
    val imageUrl: String
)

@kotlinx.serialization.Serializable
data class ContactUpdateRequest(
    val name: String,
    @kotlinx.serialization.SerialName("last_name")
    val lastName: String,
    val phone: String,
    @kotlinx.serialization.SerialName("image_url")
    val imageUrl: String
)

@kotlinx.serialization.Serializable
data class ContactBatchDeleteRequest(
    val ids: List<Long>
)