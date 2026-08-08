package com.nikaas.app.data.model

data class WeatherSignal(
    val hasRainfallAlert: Boolean = false,
    val intensity: String = "None", // e.g. "Heavy", "Light", "None"
    val area: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
