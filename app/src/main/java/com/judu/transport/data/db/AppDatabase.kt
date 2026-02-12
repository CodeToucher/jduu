package com.judu.transport.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.judu.transport.data.db.dao.RouteDao
import com.judu.transport.data.db.dao.StopDao
import com.judu.transport.data.db.dao.StopTimeDao
import com.judu.transport.data.db.dao.TripDao
import com.judu.transport.data.db.entities.Route
import com.judu.transport.data.db.entities.Stop
import com.judu.transport.data.db.entities.StopTime
import com.judu.transport.data.db.entities.Trip

@Database(entities = [Stop::class, Route::class, Trip::class, StopTime::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stopDao(): StopDao
    abstract fun routeDao(): RouteDao
    abstract fun tripDao(): TripDao
    abstract fun stopTimeDao(): StopTimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "judu_transport_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
