package com.judu.transport.data.api

import retrofit2.http.GET
import retrofit2.http.Streaming

interface RealtimeApi {
    @GET("gps_full.txt")
    @Streaming
    suspend fun getRealtimeData(): String
}
