package com.judu.transport.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.judu.transport.data.db.entities.Route
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM routes")
    fun getAllRoutes(): Flow<List<Route>>

    @Query("SELECT * FROM routes WHERE route_id = :routeId")
    suspend fun getRoute(routeId: String): Route?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routes: List<Route>)

    @Query("DELETE FROM routes")
    suspend fun clearAll()
}
