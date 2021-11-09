package com.example.actionsfun.util

import com.example.actionsfun.model.Error
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

/**
 * Parse retrofit responses' errors
 */
object ErrorUtils {
    fun parseError(response: Response<*>, retrofit: Retrofit): Error? {
        val converter = retrofit.responseBodyConverter<Error>(Error::class.java, emptyArray())
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            Error()
        }
    }
}