package com.nikaas.app.data.model

data class TrafficSignal(
    val congestionLevel: String = "Normal", // e.g. "Heavy Congestion", "Moderate", "Normal"
    val averageSpeedKmph: Int = 45,
    val area: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
