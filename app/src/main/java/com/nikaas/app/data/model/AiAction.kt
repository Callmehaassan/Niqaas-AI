package com.nikaas.app.data.model

data class AiAction(
    val id: String = "",
    val type: String = "", // "REROUTE", "DISPATCH", "ALERT"
    val description: String = "",
    var status: String = "Pending Approval", // "Pending Approval", "Executing", "Completed"
    val timestamp: Long = System.currentTimeMillis()
)
