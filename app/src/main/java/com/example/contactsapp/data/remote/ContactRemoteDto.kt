package com.example.contactsapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContactRemoteDto(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("name")
    val name: String,
    @SerialName("last_name")
    val lastName: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("image_url")
    val imageUrl: String,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)