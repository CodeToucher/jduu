package com.judu.transport.data.repository

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import com.judu.transport.data.db.AppDatabase
import com.judu.transport.data.gtfs.GtfsParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream

class GtfsRepository(private val context: Context, private val database: AppDatabase) {

    private val gtfsUrl = "https://www.stops.lt/vilnius/vilnius/gtfs.zip"

    suspend fun updateGtfsData() {
        withContext(Dispatchers.IO) {
            try {
                // 1. Download ZIP
                val zipFile = File(context.cacheDir, "gtfs.zip")
                val url = URL(gtfsUrl)
                val connection = url.openConnection()
                connection.connect()
                
                val input = BufferedInputStream(url.openStream())
                val output = FileOutputStream(zipFile)
                
                val data = ByteArray(1024)
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    output.write(data, 0, count)
                }
                output.flush()
                output.close()
                input.close()

                // 2. Unzip and Parse
                val zipIs = ZipInputStream(BufferedInputStream(java.io.FileInputStream(zipFile)))
                var entry = zipIs.nextEntry
                
                while (entry != null) {
                    when (entry.name) {
                        "stops.txt" -> {
                            val stops = GtfsParser.parseStops(zipIs)
                            database.stopDao().clearAll()
                            database.stopDao().insertAll(stops)
                            Log.d("GtfsRepository", "Inserted ${stops.size} stops")
                        }
                        "routes.txt" -> {
                            val routes = GtfsParser.parseRoutes(zipIs)
                            database.routeDao().clearAll()
                            database.routeDao().insertAll(routes)
                            Log.d("GtfsRepository", "Inserted ${routes.size} routes")
                        }
                        "trips.txt" -> {
                            val trips = GtfsParser.parseTrips(zipIs)
                            database.tripDao().clearAll()
                            // Use transaction for bulk insert - significantly faster
                            database.withTransaction {
                                trips.chunked(2000).forEach { 
                                    database.tripDao().insertAll(it)
                                }
                            }
                            Log.d("GtfsRepository", "Inserted ${trips.size} trips")
                        }
                        "stop_times.txt" -> {
                            // TODO: Stream parsing would be better here to avoid OOM, but transaction helps speed
                            val stopTimes = GtfsParser.parseStopTimes(zipIs)
                            database.stopTimeDao().clearAll()
                            database.withTransaction {
                                stopTimes.chunked(2000).forEach {
                                    database.stopTimeDao().insertAll(it)
                                }
                            }
                            Log.d("GtfsRepository", "Inserted ${stopTimes.size} stop_times")
                        }
                    }
                    zipIs.closeEntry()
                    entry = zipIs.nextEntry
                }
                zipIs.close()
                
            } catch (e: Exception) {
                Log.e("GtfsRepository", "Error updating GTFS data", e)
            }
        }
    }
}
