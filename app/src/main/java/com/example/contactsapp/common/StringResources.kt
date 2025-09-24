package com.example.contactsapp.common

import android.content.Context
import com.example.contactsapp.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResources @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getErrorName(): String = context.getString(R.string.err_name_required)
    fun getErrorLastName(): String = context.getString(R.string.err_lastname_required)
    fun getErrorPhone(): String = context.getString(R.string.err_phone_invalid)
    fun getErrorUnknown(): String = context.getString(R.string.err_unknown)
    fun getErrorFixFields(): String = context.getString(R.string.err_fix_fields)
    fun getErrorSaveContact(): String = context.getString(R.string.err_save_contact)

    fun getApiErrorUnknown(): String = context.getString(R.string.api_error_unknown)
    fun getApiErrorEmptyResponse(): String = context.getString(R.string.api_error_empty_response)
    fun getApiErrorNoInternet(): String = context.getString(R.string.api_error_no_internet)
    fun getApiErrorTimeout(): String = context.getString(R.string.api_error_timeout)
    fun getApiErrorBadRequest(): String = context.getString(R.string.api_error_bad_request)
    fun getApiErrorUnauthorized(): String = context.getString(R.string.api_error_unauthorized)
    fun getApiErrorForbidden(): String = context.getString(R.string.api_error_forbidden)
    fun getApiErrorNotFound(): String = context.getString(R.string.api_error_not_found)
    fun getApiErrorServerError(): String = context.getString(R.string.api_error_server_error)

    fun getSyncSuccess(): String = context.getString(R.string.sync_success)
    fun getSyncError(): String = context.getString(R.string.sync_error)

    fun getCountryCode(): String = context.getString(R.string.country_code)
    fun getPhonePrefix(): String = context.getString(R.string.country_code) + StringConstants.SPACE

    companion object {

        const val ERR_FIX_FIELDS = "ERR_FIX_FIELDS"
        const val ERR_NAME = "ERR_NAME"
        const val ERR_LASTNAME = "ERR_LASTNAME"
        const val ERR_PHONE = "ERR_PHONE"
        const val ERR_UNKNOWN = "ERR_UNKNOWN"
    }
}