package com.nikaas.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherService {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

data class WeatherResponse(
    val name: String,
    val weather: List<WeatherDescription>,
    val main: MainData,
    val rain: RainData? = null
)

data class WeatherDescription(
    val main: String,
    val description: String
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class RainData(
    @com.google.gson.annotations.SerializedName("1h") val rain1h: Double? = null
)

object WeatherApiClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    val service: OpenWeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherService::class.java)
    }
}
