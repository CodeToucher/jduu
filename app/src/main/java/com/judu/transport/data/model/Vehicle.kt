package com.judu.transport.data.model

data class Vehicle(
    val transportType: String, // Autobusai, Troleibusai
    val routeNumber: String,
    val vehicleId: String,
    val lat: Double,
    val lon: Double,
    val bearing: Int,
    val tripId: String? = null
)
