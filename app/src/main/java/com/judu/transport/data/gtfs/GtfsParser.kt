package com.judu.transport.data.gtfs

import com.judu.transport.data.db.entities.Route
import com.judu.transport.data.db.entities.Stop
import com.judu.transport.data.db.entities.StopTime
import com.judu.transport.data.db.entities.Trip
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object GtfsParser {

    fun parseStops(inputStream: InputStream): List<Stop> {
        val stops = mutableListOf<Stop>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var header: Map<String, Int>? = null
        
        reader.forEachLine { line ->
            val parts = line.split(",") 
            if (header == null) {
                header = parts.mapIndexed { index, name -> name to index }.toMap()
            } else {
                try {
                    val id = parts[header!!["stop_id"]!!]
                    val code = parts.getOrNull(header!!["stop_code"] ?: -1)
                    val name = parts[header!!["stop_name"]!!]
                    val lat = parts[header!!["stop_lat"]!!].toDouble()
                    val lon = parts[header!!["stop_lon"]!!].toDouble()
                    val type = parts.getOrNull(header!!["location_type"] ?: -1)?.toIntOrNull()

                    stops.add(Stop(id, code, name, lat, lon, type))
                } catch (e: Exception) {
                    // Skip malformed lines
                }
            }
        }
        return stops
    }

    fun parseRoutes(inputStream: InputStream): List<Route> {
        val routes = mutableListOf<Route>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var header: Map<String, Int>? = null

        reader.forEachLine { line ->
            val parts = line.split(",") 
            if (header == null) {
                header = parts.mapIndexed { index, name -> name to index }.toMap()
            } else {
                try {
                    val id = parts[header!!["route_id"]!!]
                    val shortName = parts[header!!["route_short_name"]!!]
                    val longName = parts[header!!["route_long_name"]!!]
                    val type = parts[header!!["route_type"]!!].toInt()
                    val color = parts.getOrNull(header!!["route_color"] ?: -1)
                    val textColor = parts.getOrNull(header!!["route_text_color"] ?: -1)

                    routes.add(Route(id, shortName, longName, type, color, textColor))
                } catch (e: Exception) {
                }
            }
        }
        return routes
    }

    fun parseTrips(inputStream: InputStream): List<Trip> {
        val trips = mutableListOf<Trip>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var header: Map<String, Int>? = null

        reader.forEachLine { line ->
            val parts = line.split(",") 
            if (header == null) {
                header = parts.mapIndexed { index, name -> name to index }.toMap()
            } else {
                try {
                    val tripId = parts[header!!["trip_id"]!!]
                    val routeId = parts[header!!["route_id"]!!]
                    val serviceId = parts[header!!["service_id"]!!]
                    val headsign = parts.getOrNull(header!!["trip_headsign"] ?: -1)
                    val directionId = parts.getOrNull(header!!["direction_id"] ?: -1)?.toIntOrNull()

                    trips.add(Trip(tripId, routeId, serviceId, headsign, directionId))
                } catch (e: Exception) {
                }
            }
        }
        return trips
    }

    fun parseStopTimes(inputStream: InputStream): List<StopTime> {
        val stopTimes = mutableListOf<StopTime>()
        val reader = BufferedReader(InputStreamReader(inputStream))
        var header: Map<String, Int>? = null

        reader.forEachLine { line ->
            val parts = line.split(",") 
            if (header == null) {
                header = parts.mapIndexed { index, name -> name to index }.toMap()
            } else {
                try {
                    val tripId = parts[header!!["trip_id"]!!]
                    val stopId = parts[header!!["stop_id"]!!]
                    val sequence = parts[header!!["stop_sequence"]!!].toInt()
                    val arrival = parts.getOrNull(header!!["arrival_time"] ?: -1)
                    val departure = parts.getOrNull(header!!["departure_time"] ?: -1)

                    stopTimes.add(StopTime(tripId, stopId, sequence, arrival, departure))
                } catch (e: Exception) {
                }
            }
        }
        return stopTimes
    }
}
