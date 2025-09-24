package com.example.contactsapp.data.remote

import com.example.contactsapp.common.StringConstants

sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    data class Exception<T>(val throwable: Throwable) : NetworkResult<T>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                NetworkResult.Success(body)
            } ?: NetworkResult.Error(StringConstants.API_ERROR_EMPTY_RESPONSE)
        } else {
            NetworkResult.Error(
                message = response.message() ?: StringConstants.API_ERROR_UNKNOWN,
                code = response.code()
            )
        }
    } catch (e: Exception) {
        NetworkResult.Exception(e)
    }
}