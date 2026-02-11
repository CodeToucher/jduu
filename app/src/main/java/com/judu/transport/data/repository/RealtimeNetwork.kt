package com.judu.transport.data.repository

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RealtimeNetwork {
    private const val BASE_URL = "https://www.stops.lt/vilnius/"

    val api: com.judu.transport.data.api.RealtimeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(com.judu.transport.data.api.RealtimeApi::class.java)
    }
}
