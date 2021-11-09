package com.example.actionsfun.repository

import com.example.actionsfun.data.repository.BaseRepository
import com.example.actionsfun.model.Action
import com.example.actionsfun.model.ActionResponse
import com.example.actionsfun.model.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MockActionsRepository @Inject constructor() : BaseRepository<ActionResponse> {

    val mockActions = listOf(
        Action(1, "animation", false, 10, listOf(0,1,2), 1000L),
        Action(2, "toast", true, 9, listOf(3,4,5), 1000L),
        Action(3, "call", false, 11, listOf(0,1,6), 1000L),
        Action(4, "notification", true, 7, listOf(2,4,6), 1000L),
    )

    val actions: ResultState<ActionResponse> = ResultState.success(ActionResponse(mockActions))

    override suspend fun fetchAll(): Flow<ResultState<ActionResponse>?> {
        return flow {
            emit(fetchAllCached())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun fetchAllCached(): ResultState<ActionResponse> {
        return actions
    }

    override suspend fun fetchByIdCached(id: Int): ResultState<ActionResponse>? {
        for(action in mockActions) {
            if (action.id == id) {
                return ResultState.success(ActionResponse(listOf(action)))
            }
        }
        return ResultState.failure(null, "couldn't find")
    }

    override suspend fun update(id: Int) {
        TODO("Not yet implemented")
    }
}