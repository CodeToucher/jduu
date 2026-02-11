package com.judu.transport.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judu.transport.data.db.entities.Trip

@Dao
interface TripDao {
    @Query("SELECT * FROM trips WHERE route_id = :routeId")
    suspend fun getTripsForRoute(routeId: String): List<Trip>
    
    // Get one trip per direction to show stops
    @Query("SELECT * FROM trips WHERE route_id = :routeId GROUP BY direction_id")
    suspend fun getRepresentativeTrips(routeId: String): List<Trip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(trips: List<Trip>)
    
    @Query("DELETE FROM trips")
    suspend fun clearAll()
}
