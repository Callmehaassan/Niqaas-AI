package com.nikaas.app.data.model

data class ZoneOutcome(
    val area: String = "",
    val trafficStatus: String = "Normal", // e.g. "Congested", "Rerouted / Clear"
    val dispatchStatus: String = "Idle", // e.g. "Idle", "Dispatched", "Arrived", "Cleared"
    val alertsStatus: String = "None" // e.g. "None", "Sent to local residents"
)
