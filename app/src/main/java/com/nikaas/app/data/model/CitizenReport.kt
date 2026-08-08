package com.nikaas.app.data.model

data class CitizenReport(
    val id: String = "",
    val location: String = "",
    val description: String = "",
    val photoUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
