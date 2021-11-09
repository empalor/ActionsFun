package com.example.actionsfun.data.repository

import android.content.Context
import com.example.actionsfun.data.local.ActionDao
import com.example.actionsfun.data.remote.ActionRemoteDataSource
import com.example.actionsfun.model.Action
import com.example.actionsfun.model.ActionResponse
import com.example.actionsfun.model.ResultState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject

/**
 * Fetches data(actions) from local/remote data sources
 */
class ActionsRepository @Inject constructor(
    private val actionRemoteDataSource: ActionRemoteDataSource,
    private val actionDao: ActionDao,
    private val applicationContext: Context
) : BaseRepository<ActionResponse> {

    /**
     * Attempt to fetch actions from: cache -> remote -> assets
     */
    override suspend fun fetchAll(): Flow<ResultState<ActionResponse>?> {
        return flow {
            emit(fetchAllCached())
            emit(ResultState.loading())
            var result = actionRemoteDataSource.fetchAllActions()

            // when remote fetching succeeds, we have to cache/persist results,
            // here we will save to our only "source of truth, database"
            if (result.state == ResultState.State.SUCCESS) {
                result.data?.results.let {
                    //can be done also done by,
//                    actionDao.updateAll(it)

                    actionDao.deleteAll(it)
                    actionDao.insertAll(it!!)
                }
            } else if (result.state == ResultState.State.FAILURE) {
                //Failed fetching from remote, let's try assets (only as a last resort)
                val listType: Type = object : TypeToken<ArrayList<Action?>?>() {}.type
                val data = Gson().fromJson<ArrayList<Action>>(readJsonAsset(), listType)
                result = ResultState.success(ActionResponse(data))
                actionDao.deleteAll(data)
                actionDao.insertAll(data)
            }
            emit(result)
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun fetchAllCached(): ResultState<ActionResponse>? {
        return actionDao.getAll()?.let {
            ResultState.success(ActionResponse(it))
        }
    }


    override suspend fun fetchByIdCached(id: Int): ResultState<ActionResponse>? {
        return actionDao.getById(id)?.let {
            ResultState.success(ActionResponse(listOf(it)))
        }
    }

    override suspend fun update(id: Int) {
        val result = fetchByIdCached(id)
        result.let {
            val action: ResultState<ActionResponse> =
                ResultState.success(ActionResponse(it?.data?.results))
            actionDao.update(action.data?.results?.get(0)!!)
        }
    }

    @Throws(IOException::class)
    private fun readJsonAsset(): String {
        val inputStream = applicationContext.assets.open("actions_config.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }
}