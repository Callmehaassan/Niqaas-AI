package com.nikaas.app.domain.model

import com.nikaas.app.data.model.AiAction
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.data.model.ZoneOutcome

data class FusedIncident(
    val id: String = "",
    val area: String = "",
    val severity: String = "Low", // "Low", "Medium", "High"
    val confidenceScore: Int = 0,
    val confidenceReasoning: String = "",
    val citizenReports: List<CitizenReport> = emptyList(),
    val weatherSignal: WeatherSignal = WeatherSignal(),
    val trafficSignal: TrafficSignal = TrafficSignal(),
    val actions: List<AiAction> = emptyList(),
    val beforeOutcome: ZoneOutcome = ZoneOutcome(),
    var afterOutcome: ZoneOutcome = ZoneOutcome(),
    var isApproved: Boolean = false,
    var isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
