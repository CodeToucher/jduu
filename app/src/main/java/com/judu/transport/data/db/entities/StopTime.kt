package com.judu.transport.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stop_times", primaryKeys = ["trip_id", "stop_sequence"])
data class StopTime(
    val trip_id: String,
    val stop_id: String,
    val stop_sequence: Int,
    val arrival_time: String?,
    val departure_time: String?
)
