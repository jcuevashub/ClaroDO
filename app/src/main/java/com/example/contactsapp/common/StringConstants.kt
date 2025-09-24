package com.example.contactsapp.common

object StringConstants {

    // Error codes
    const val ERR_FIX_FIELDS = "ERR_FIX_FIELDS"
    const val ERR_NAME = "El nombre no puede estar vacío"
    const val ERR_LASTNAME = "El apellido no puede estar vacío"
    const val ERR_PHONE = "El teléfono no puede estar vacío"
    const val ERR_UNKNOWN = "Algo salió mal, por favor intenta más tarde"

    // URLs and formats
    const val DEFAULT_AVATAR_URL = "https://picsum.photos/200"
    const val RANDOM_AVATAR_URL_TEMPLATE = "https://picsum.photos/200?random="

    // Database related
    const val DATABASE_NAME = "contact_db"
    const val TABLE_CONTACTS = "contacts"

    // UI related
    const val EMPTY_STRING = ""
    const val SPACE = " "
    const val PHONE_PREFIX = "+1 "
    const val PHONE_SEPARATOR = "-"
    const val PERCENT_WRAPPER = "%"

    // Phone formatting
    const val COUNTRY_CODE_1 = "1"

    // Navigation routes
    const val ROUTE_CONTACT_LIST = "contact_list"
    const val ROUTE_CREATE_CONTACT = "create_contact"

    // SQL queries
    const val QUERY_ALL_CONTACTS = "SELECT * FROM contacts ORDER BY name ASC"
    const val QUERY_SEARCH_CONTACTS = "SELECT * FROM contacts WHERE name LIKE :query OR lastName LIKE :query OR phone LIKE :query ORDER BY name ASC"

    // Package name for testing
    const val PACKAGE_NAME = "com.example.contactsapp"

    // API endpoints
    const val API_BASE_URL = "https://api.example.com/v1/"
    const val API_CONTACTS_ENDPOINT = "contacts"
    const val API_CONTACTS_BATCH_DELETE_ENDPOINT = "contacts/batch-delete"

    // API parameters
    const val API_QUERY_PARAM = "q"
    const val API_ID_PARAM = "id"

    // Network constants
    const val JSON_MEDIA_TYPE = "application/json"

    // API error messages
    const val API_ERROR_UNKNOWN = "Error desconocido de la API"
    const val API_ERROR_EMPTY_RESPONSE = "Respuesta vacía del servidor"
    const val API_ERROR_NO_INTERNET = "Sin conexión a internet"
    const val API_ERROR_TIMEOUT = "Tiempo de espera agotado"
    const val API_ERROR_BAD_REQUEST = "Solicitud inválida"
    const val API_ERROR_UNAUTHORIZED = "No autorizado"
    const val API_ERROR_FORBIDDEN = "Acceso prohibido"
    const val API_ERROR_NOT_FOUND = "Recurso no encontrado"
    const val API_ERROR_SERVER_ERROR = "Error interno del servidor"
}