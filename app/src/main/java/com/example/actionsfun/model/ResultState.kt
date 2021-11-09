package com.example.actionsfun.model

/**
 * Generic data holder for fetching-states-data
 */
data class ResultState<T>(
    val state: State,
    val data: T?,
    val error: Error?,
    val message: String?
) {

    enum class State {
        SUCCESS,
        FAILURE,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): ResultState<T> {
            return ResultState(State.SUCCESS, data, null, null)
        }

        fun <T> failure(error: Error?, message: String?): ResultState<T> {
            return ResultState(State.FAILURE, null, error, message)
        }

        fun <T> loading(data: T? = null): ResultState<T> {
            return ResultState(State.LOADING, data, null, null)
        }
    }

    override fun toString(): String {
        return "Result: (state=$state, data=$data, failure error=$error, message=$message)"
    }
}