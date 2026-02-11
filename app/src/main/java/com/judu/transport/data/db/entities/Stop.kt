package com.judu.transport.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stops")
data class Stop(
    @PrimaryKey val stop_id: String,
    val stop_code: String?,
    val stop_name: String,
    val stop_lat: Double,
    val stop_lon: Double,
    val location_type: Int? // 0 or empty: stop, 1: station
)
