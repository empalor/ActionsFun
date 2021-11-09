package com.example.actionsfun.data.local

import androidx.room.*
import com.example.actionsfun.model.Action

@Dao
interface ActionDao {

    @Query("SELECT * FROM actions order by priority DESC")
    fun getAll(): List<Action>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(movies: List<Action>)

    @Update
    fun update(action: Action)

    @Update
    fun updateAll(actions: List<Action>?)

    @Delete
    fun delete(action: Action)

    @Delete
    fun deleteAll(actions: List<Action>?)

    @Query("SELECT * FROM actions WHERE id =:id")
    fun getById(id: Int): Action?
}