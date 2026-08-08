package com.nikaas.app.data.repository

import com.nikaas.app.data.api.GeminiApiService
import com.nikaas.app.data.local.MockDataProvider
import com.nikaas.app.data.model.AiAction
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.data.model.ZoneOutcome
import com.nikaas.app.domain.model.FusedIncident
import java.util.UUID

class NikaasRepositoryImpl(
    private val apiService: GeminiApiService = GeminiApiService()
) : NikaasRepository {

    override fun getSectors(): List<String> = MockDataProvider.SECTORS

    override fun getCitizenReports(): List<CitizenReport> = MockDataProvider.activeCitizenReports

    override fun getCitizenReportsForArea(area: String): List<CitizenReport> {
        val normalizedArea = area.trim()
        return MockDataProvider.activeCitizenReports.filter { 
            it.location.contains(normalizedArea, ignoreCase = true) ||
            normalizedArea.contains(it.location, ignoreCase = true)
        }
    }

    override fun submitCitizenReport(report: CitizenReport) {
        MockDataProvider.activeCitizenReports.add(0, report.copy(id = UUID.randomUUID().toString()))
    }

    override fun getMockWeather(area: String): WeatherSignal = MockDataProvider.getMockWeather(area)

    override fun getMockTraffic(area: String): TrafficSignal = MockDataProvider.getMockTraffic(area)

    override suspend fun fuseAndCreateIncident(area: String): FusedIncident {
        val weather = getMockWeather(area)
        val traffic = getMockTraffic(area)
        val reports = getCitizenReportsForArea(area)

        val response = apiService.fuseSignals(reports, weather, traffic)

        // Convert response DTO actions to Domain models
        val actions = response.actions.map { actionDto ->
            AiAction(
                id = UUID.randomUUID().toString(),
                type = actionDto.type,
                description = actionDto.description,
                status = "Pending Approval"
            )
        }

        // Setup the baseline "before" state of the incident zone
        val beforeOutcome = ZoneOutcome(
            area = area,
            trafficStatus = if (traffic.congestionLevel.contains("Heavy")) "Heavy Congestion" else traffic.congestionLevel,
            dispatchStatus = "Idle (Pending Approval)",
            alertsStatus = "None Sent"
        )

        val incident = FusedIncident(
            id = UUID.randomUUID().toString(),
            area = area,
            severity = response.severity,
            confidenceScore = response.confidenceScore,
            confidenceReasoning = response.confidenceReasoning,
            citizenReports = reports,
            weatherSignal = weather,
            trafficSignal = traffic,
            actions = actions,
            beforeOutcome = beforeOutcome,
            afterOutcome = beforeOutcome.copy() // Starts identical to before state
        )

        MockDataProvider.fusedIncidents.add(0, incident)
        return incident
    }

    override fun getFusedIncidents(): List<FusedIncident> = MockDataProvider.fusedIncidents

    override fun getIncidentById(id: String): FusedIncident? {
        return MockDataProvider.fusedIncidents.find { it.id == id }
    }

    override fun approveIncident(id: String) {
        val incident = getIncidentById(id) ?: return
        incident.isApproved = true
        
        // Advance all action statuses to Executing
        incident.actions.forEach {
            it.status = "Executing"
        }
        
        // Simulates tactical response execution (Updating simulated Outcomes)
        var trafficOutcome = incident.beforeOutcome.trafficStatus
        var dispatchOutcome = "Pending"
        var alertsOutcome = "None"

        incident.actions.forEach { action ->
            when (action.type) {
                "REROUTE" -> trafficOutcome = "Clear (Rerouted via Service Rd)"
                "DISPATCH" -> dispatchOutcome = "Dispatched (WASA Team Active)"
                "ALERT" -> alertsOutcome = "Sent (SMS alert sent to residents)"
            }
        }

        incident.afterOutcome = ZoneOutcome(
            area = incident.area,
            trafficStatus = trafficOutcome,
            dispatchStatus = dispatchOutcome,
            alertsStatus = alertsOutcome
        )
    }

    override fun resolveIncident(id: String) {
        val incident = getIncidentById(id) ?: return
        incident.isResolved = true
        
        // Complete all actions
        incident.actions.forEach {
            it.status = "Completed"
        }
        
        // Simulate a fully cleared drainage zone
        incident.afterOutcome = ZoneOutcome(
            area = incident.area,
            trafficStatus = "Clear / Flowing",
            dispatchStatus = "Cleared / Resolved",
            alertsStatus = "Resolved"
        )
    }
}
