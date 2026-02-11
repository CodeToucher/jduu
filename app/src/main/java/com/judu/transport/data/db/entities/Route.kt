package com.judu.transport.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey val route_id: String,
    val route_short_name: String,
    val route_long_name: String,
    val route_type: Int, // 0: Tram, 3: Bus, 700: Bus Service (varies)
    val route_color: String?,
    val route_text_color: String?
)
