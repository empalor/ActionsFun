package com.example.actionsfun.di

import android.content.Context
import androidx.room.Room
import com.example.actionsfun.data.local.ActionDao
import com.example.actionsfun.data.local.ActionsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataModule {

    @Singleton
    @Provides
    fun provideActionsDb(@ApplicationContext context: Context): ActionsDatabase {
        return Room.databaseBuilder(
            context,
            ActionsDatabase::class.java,
            "${ActionsDatabase.DATABASE_NAME}.db"
        ).build()
    }

    @Provides
    fun provideActionDAO(actionsDatabase: ActionsDatabase): ActionDao {
        return actionsDatabase.actionDao()
    }
}