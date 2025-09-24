package com.example.contactsapp.common

object StringConstants {

    const val ERR_FIX_FIELDS = "ERR_FIX_FIELDS"
    const val ERR_NAME = "ERR_NAME"
    const val ERR_LASTNAME = "ERR_LASTNAME"
    const val ERR_PHONE = "ERR_PHONE"
    const val ERR_UNKNOWN = "ERR_UNKNOWN"

    const val DEFAULT_AVATAR_URL = "https://picsum.photos/200"
    const val RANDOM_AVATAR_URL_TEMPLATE = "https://picsum.photos/200?random="

    const val DATABASE_NAME = "contact_db"
    const val TABLE_CONTACTS = "contacts"

    const val EMPTY_STRING = ""
    const val SPACE = " "
    const val PHONE_PREFIX = "+1 "
    const val PHONE_SEPARATOR = "-"
    const val PERCENT_WRAPPER = "%"

    const val COUNTRY_CODE_1 = "1"

    const val ROUTE_CONTACT_LIST = "contact_list"
    const val ROUTE_CREATE_CONTACT = "create_contact"

    const val QUERY_ALL_CONTACTS = "SELECT * FROM contacts ORDER BY name ASC"
    const val QUERY_SEARCH_CONTACTS = "SELECT * FROM contacts WHERE name LIKE :query OR lastName LIKE :query OR phone LIKE :query ORDER BY name ASC"

    const val PACKAGE_NAME = "com.example.contactsapp"

    const val API_BASE_URL = "https://api.example.com/v1/"
    const val API_CONTACTS_ENDPOINT = "contacts"
    const val API_CONTACTS_BATCH_DELETE_ENDPOINT = "contacts/batch-delete"

    const val API_QUERY_PARAM = "q"
    const val API_ID_PARAM = "id"

    const val JSON_MEDIA_TYPE = "application/json"

    const val API_ERROR_UNKNOWN = "API_ERROR_UNKNOWN"
    const val API_ERROR_EMPTY_RESPONSE = "API_ERROR_EMPTY_RESPONSE"
    const val API_ERROR_NO_INTERNET = "API_ERROR_NO_INTERNET"
    const val API_ERROR_TIMEOUT = "API_ERROR_TIMEOUT"
    const val API_ERROR_BAD_REQUEST = "API_ERROR_BAD_REQUEST"
    const val API_ERROR_UNAUTHORIZED = "API_ERROR_UNAUTHORIZED"
    const val API_ERROR_FORBIDDEN = "API_ERROR_FORBIDDEN"
    const val API_ERROR_NOT_FOUND = "API_ERROR_NOT_FOUND"
    const val API_ERROR_SERVER_ERROR = "API_ERROR_SERVER_ERROR"

    const val LANG_ENGLISH = "en"
    const val LANG_SPANISH = "es"
}