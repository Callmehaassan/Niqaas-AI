package com.nikaas.app.data.model

data class CitizenReport(
    val id: String = "",
    val location: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    val blockageType: String = "Unknown",
    val blockageSeverity: String = "Low",
    val reporterUid: String = "",
    val reporterName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
