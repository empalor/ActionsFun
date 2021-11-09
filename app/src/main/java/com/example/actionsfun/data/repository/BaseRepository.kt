package com.example.actionsfun.data.repository

import com.example.actionsfun.model.ResultState
import kotlinx.coroutines.flow.Flow

/**
 * Base for local/remote repositories implementations.
 */
interface BaseRepository<E> {
    suspend fun fetchAll(): Flow<ResultState<E>?>
    suspend fun fetchAllCached(): ResultState<E>?
    suspend fun fetchByIdCached(id: Int): ResultState<E>?
    suspend fun update(id: Int)
}