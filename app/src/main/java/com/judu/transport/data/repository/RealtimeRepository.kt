package com.judu.transport.data.repository

import android.util.Log
import com.judu.transport.data.api.RealtimeApi
import com.judu.transport.data.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RealtimeRepository(private val api: RealtimeApi) {

    fun getRealtimeVehicles(intervalMs: Long = 5000): Flow<List<Vehicle>> = flow {
        while (true) {
            try {
                val csvData = api.getRealtimeData()
                val vehicles = parseGpsData(csvData)
                emit(vehicles)
            } catch (e: Exception) {
                Log.e("RealtimeRepository", "Error fetching realtime data", e)
                emit(emptyList()) // Emit empty or previous state on error
            }
            delay(intervalMs)
        }
    }.flowOn(Dispatchers.IO)

    private fun parseGpsData(csvData: String): List<Vehicle> {
        val vehicles = mutableListOf<Vehicle>()
        val lines = csvData.lines()
        
        // Find header index
        if (lines.isEmpty()) return emptyList()
        
        val header = lines.first().split(",")
        val idxValues = header.mapIndexed { index, name -> name to index }.toMap()

        // Indices
        val idxTransport = idxValues["Transportas"]
        val idxRoute = idxValues["Marsrutas"]
        val idxId = idxValues["MasinosNumeris"]
        val idxLat = idxValues["Platuma"]
        val idxLon = idxValues["Ilguma"]
        val idxBearing = idxValues["Azimutas"]
        val idxTripId = idxValues["ReisoID"] // Or ReisoIdGTFS

        if (idxLat == null || idxLon == null || idxRoute == null) return emptyList()

        for (i in 1 until lines.size) {
            val line = lines[i]
            if (line.isBlank()) continue
            
            val parts = line.split(",")
            try {
                // Lat/Lon are integers * 1000000
                // e.g. 54621436 -> 54.621436
                val lat = parts[idxLat].toInt() / 1_000_000.0
                val lon = parts[idxLon].toInt() / 1_000_000.0
                val route = parts[idxRoute]
                val type = idxTransport?.let { parts[it] } ?: ""
                val id = idxId?.let { parts[it] } ?: ""
                val bearing = idxBearing?.let { parts[it].toIntOrNull() } ?: 0
                val trip = idxTripId?.let { parts[it] }

                vehicles.add(Vehicle(type, route, id, lat, lon, bearing, trip))
            } catch (e: Exception) {
                // Ignore malformed lines
            }
        }
        return vehicles
    }
}
