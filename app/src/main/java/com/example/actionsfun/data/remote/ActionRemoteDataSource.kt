package com.example.actionsfun.data.remote

import android.util.Log
import com.example.actionsfun.model.ActionResponse
import com.example.actionsfun.model.ResultState
import com.example.actionsfun.services.ActionService
import com.example.actionsfun.util.ErrorUtils
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

class ActionRemoteDataSource @Inject constructor(
    private val retrofit: Retrofit,
    private val actionService: ActionService
) {

    companion object {
        private val TAG = ActionRemoteDataSource::class.qualifiedName
        private const val ERROR_FETCHING_ACTIONS = "error fetching actions remotely"
    }

    suspend fun fetchAllActions(): ResultState<ActionResponse> {
        return getResponse(
            request = { actionService.getActionsConfig() },
            ERROR_FETCHING_ACTIONS
        )
    }


    /**
     * Assume we might have more requests in the future (get action by id, etc.)
     */
    private suspend fun <T> getResponse(
        request: suspend () -> Response<T>,
        defaultErrorMessage: String
    ): ResultState<T> {
        return try {
            Log.i(TAG, "Thread: ${Thread.currentThread().id}")
            val result = request.invoke()
            if (result.isSuccessful) {
                Log.i(TAG, "request succeeded")
                ResultState.success(result.body())
            } else {
                Log.i(TAG, "request failed")
                val errorResponse = ErrorUtils.parseError(result, retrofit)
                val errorMessage = errorResponse?.statusMessage ?: defaultErrorMessage
                ResultState.failure(errorResponse, errorMessage)
            }
        } catch (e: Throwable) {
            Log.i(TAG, "request failed")
            ResultState.failure(null, e.message)
        }
    }
}