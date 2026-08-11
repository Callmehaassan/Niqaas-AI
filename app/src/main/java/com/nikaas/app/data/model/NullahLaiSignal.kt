package com.nikaas.app.data.model

data class NullahLaiSignal(
    val kattarianWaterLevelFt: Double = 0.0,
    val gawalmandiWaterLevelFt: Double = 0.0,
    val catchmentRainfallMm: Double = 0.0,
    val status: String = "Normal", // "Normal", "Alert", "Warning", "Danger"
    val timestamp: Long = System.currentTimeMillis()
)
