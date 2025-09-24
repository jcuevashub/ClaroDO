package com.example.contactsapp.data.local

import com.example.contactsapp.domain.model.Contact

fun ContactEntity.toDomain(): Contact {
    return Contact(
        id = id,
        name = name,
        lastName = lastName,
        phone = phone,
        imageUrl = imageUrl
    )
}

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        name = name,
        lastName = lastName,
        phone = phone,
        imageUrl = imageUrl
    )
}