package com.example.actionsfun.services

import com.example.actionsfun.util.NetworkConfig
import com.example.actionsfun.model.ActionResponse
import retrofit2.Response
import retrofit2.http.GET

interface ActionService {
    @GET(NetworkConfig.BASE_URL)
    suspend fun getActionsConfig() : Response<ActionResponse>
}