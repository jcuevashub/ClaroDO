package com.example.contactsapp.domain.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val lastName: String,
    val phone: String,
    val imageUrl: String
)