package com.nikaas.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("departure_time") departureTime: String = "now",
        @Query("key") apiKey: String
    ): DirectionsResponse
}

data class DirectionsResponse(
    val status: String,
    val routes: List<DirectionsRoute>
)

data class DirectionsRoute(
    val legs: List<DirectionsLeg>
)

data class DirectionsLeg(
    val duration: DurationTextVal,
    val duration_in_traffic: DurationTextVal? = null,
    val distance: DistanceTextVal
)

data class DurationTextVal(
    val text: String,
    val value: Int // seconds
)

data class DistanceTextVal(
    val text: String,
    val value: Int // meters
)

object TrafficApiClient {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val service: GoogleDirectionsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDirectionsService::class.java)
    }
}
