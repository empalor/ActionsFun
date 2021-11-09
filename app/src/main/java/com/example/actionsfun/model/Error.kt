package com.example.actionsfun.model

/**
 * Represents error in fetching data from a local/remote repository.
 */
data class Error(
    val statusCode: Int = 0,
    val statusMessage: String? = null
)