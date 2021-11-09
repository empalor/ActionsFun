package com.example.actionsfun.di

import android.content.Context
import com.example.actionsfun.data.local.ActionDao
import com.example.actionsfun.data.remote.ActionRemoteDataSource
import com.example.actionsfun.data.repository.ActionsRepository
import com.example.actionsfun.data.repository.BaseRepository
import com.example.actionsfun.model.ActionResponse
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideActionsRepository(
        actionDao: ActionDao,
        actionRemoteDataSource: ActionRemoteDataSource,
        @ApplicationContext applicationContext: Context
    ): BaseRepository<ActionResponse> {
        return ActionsRepository(actionRemoteDataSource, actionDao, applicationContext)
    }
}