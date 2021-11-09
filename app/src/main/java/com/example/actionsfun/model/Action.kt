package com.example.actionsfun.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actions")
data class Action(
    @NonNull
    @PrimaryKey
    val id: Int,
    val type: String?,
    val enabled: Boolean?,
    val priority: Int,
    val valid_days: List<Int>,
    val cool_down: Long,
    var last_triggered: Long = -1L // an epoch indicating last trigger time
) {
    override fun toString(): String {
        return "$type action, enabled: $enabled"
    }
}
