package com.judu.transport.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judu.transport.data.db.entities.Stop
import kotlinx.coroutines.flow.Flow

@Dao
interface StopDao {
    @Query("SELECT * FROM stops")
    fun getAllStops(): Flow<List<Stop>>

    @Query("SELECT * FROM stops WHERE stop_name LIKE '%' || :query || '%'")
    suspend fun searchStops(query: String): List<Stop>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stops: List<Stop>)
    
    @Query("DELETE FROM stops")
    suspend fun clearAll()
}
