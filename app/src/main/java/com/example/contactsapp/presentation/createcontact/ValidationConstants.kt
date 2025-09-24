package com.example.contactsapp.presentation.createcontact

object ValidationConstants {
    const val COUNTRY_CODE_PREFIX = "+1"
    val VALID_AREA_CODES = setOf("809", "829", "849")
    const val PHONE_DIGITS_LENGTH = 10
    
    // Error messages for inline validation
    const val ERR_NAME_REQUIRED = "El nombre es obligatorio"
    const val ERR_LASTNAME_REQUIRED = "El apellido es obligatorio"
    const val ERR_PHONE_INVALID = "Ingresa un teléfono válido"
    const val ERR_AREA_CODE_INVALID = "Código de área inválido (usa 809/829/849)"
}