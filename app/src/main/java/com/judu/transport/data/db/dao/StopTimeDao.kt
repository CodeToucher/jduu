package com.judu.transport.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judu.transport.data.db.entities.StopTime
import com.judu.transport.data.db.entities.Stop

@Dao
interface StopTimeDao {
    @Query("SELECT * FROM stop_times WHERE trip_id = :tripId ORDER BY stop_sequence")
    suspend fun getStopTimesForTrip(tripId: String): List<StopTime>
    
    @Query("""
        SELECT stops.* FROM stops 
        INNER JOIN stop_times ON stops.stop_id = stop_times.stop_id 
        WHERE stop_times.trip_id = :tripId 
        ORDER BY stop_times.stop_sequence
    """)
    suspend fun getStopsForTrip(tripId: String): List<Stop>

    @Query("""
        SELECT st.* 
        FROM stop_times st
        INNER JOIN trips t ON st.trip_id = t.trip_id
        WHERE t.route_id = :routeId 
          AND st.stop_id = :stopId
        ORDER BY st.arrival_time
    """)
    suspend fun getScheduleForRouteAndStop(routeId: String, stopId: String): List<StopTime>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stopTimes: List<StopTime>)

    @Query("DELETE FROM stop_times")
    suspend fun clearAll()
}
