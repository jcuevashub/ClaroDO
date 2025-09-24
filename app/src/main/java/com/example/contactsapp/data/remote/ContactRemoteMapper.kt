package com.example.contactsapp.data.remote

import com.example.contactsapp.data.local.ContactEntity
import com.example.contactsapp.domain.model.Contact

fun ContactRemoteDto.toEntity(): ContactEntity {
    return ContactEntity(
        id = this.id,
        name = this.name,
        lastName = this.lastName,
        phone = this.phone,
        imageUrl = this.imageUrl
    )
}

fun ContactRemoteDto.toDomain(): Contact {
    return Contact(
        id = this.id,
        name = this.name,
        lastName = this.lastName,
        phone = this.phone,
        imageUrl = this.imageUrl
    )
}

fun Contact.toCreateRequest(): ContactCreateRequest {
    return ContactCreateRequest(
        name = this.name,
        lastName = this.lastName,
        phone = this.phone,
        imageUrl = this.imageUrl
    )
}

fun Contact.toUpdateRequest(): ContactUpdateRequest {
    return ContactUpdateRequest(
        name = this.name,
        lastName = this.lastName,
        phone = this.phone,
        imageUrl = this.imageUrl
    )
}

fun List<Contact>.toBatchDeleteRequest(): ContactBatchDeleteRequest {
    return ContactBatchDeleteRequest(
        ids = this.map { it.id }
    )
}