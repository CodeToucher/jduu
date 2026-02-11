package com.judu.transport.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey val trip_id: String,
    val route_id: String,
    val service_id: String,
    val trip_headsign: String?,
    val direction_id: Int? // 0 or 1
)
