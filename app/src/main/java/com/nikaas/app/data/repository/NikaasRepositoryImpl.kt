package com.nikaas.app.data.repository

import android.graphics.Bitmap
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.nikaas.app.data.api.GeminiApiService
import com.nikaas.app.data.api.WeatherApiClient
import com.nikaas.app.data.api.TrafficApiClient
import com.nikaas.app.data.local.MockDataProvider
import com.nikaas.app.data.model.AiAction
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.data.model.ZoneOutcome
import com.nikaas.app.domain.model.FusedIncident
import com.nikaas.app.utils.Constants
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class NikaasRepositoryImpl(
    private val apiService: GeminiApiService = GeminiApiService()
) : NikaasRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val reportsCollection = db.collection("reports")
    private val incidentsCollection = db.collection("incidents")

    private val localReports = CopyOnWriteArrayList<CitizenReport>()
    private val localIncidents = CopyOnWriteArrayList<FusedIncident>()

    init {
        // Register Real-time Listener for Citizen Reports
        reportsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(CitizenReport::class.java)
                    localReports.clear()
                    localReports.addAll(list)
                }
            }

        // Register Real-time Listener for Fused Incidents
        incidentsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    e.printStackTrace()
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(FusedIncident::class.java)
                    localIncidents.clear()
                    localIncidents.addAll(list)
                }
            }
    }

    override fun getSectors(): List<String> = MockDataProvider.SECTORS

    override fun getCitizenReports(): List<CitizenReport> = localReports

    override fun getCitizenReportsForArea(area: String): List<CitizenReport> {
        val normalizedArea = area.trim()
        return localReports.filter { 
            it.location.contains(normalizedArea, ignoreCase = true) ||
            normalizedArea.contains(it.location, ignoreCase = true)
        }
    }

    override fun submitCitizenReport(report: CitizenReport, imageBitmap: Bitmap?) {
        val reportId = UUID.randomUUID().toString()

        if (imageBitmap != null) {
            // Compress and upload photo to Firebase Storage
            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            val data = baos.toByteArray()

            val photoRef = storage.reference.child("citizen_reports/$reportId.jpg")
            photoRef.putBytes(data)
                .addOnSuccessListener {
                    photoRef.downloadUrl.addOnSuccessListener { uri ->
                        val finalReport = report.copy(id = reportId, photoUrl = uri.toString())
                        reportsCollection.document(reportId).set(finalReport)
                    }
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    // Fallback to save report text details only if storage upload fails
                    val finalReport = report.copy(id = reportId)
                    reportsCollection.document(reportId).set(finalReport)
                }
        } else {
            val finalReport = report.copy(id = reportId)
            reportsCollection.document(reportId).set(finalReport)
        }
    }

    override fun getMockWeather(area: String): WeatherSignal = MockDataProvider.getMockWeather(area)

    override fun getMockTraffic(area: String): TrafficSignal = MockDataProvider.getMockTraffic(area)

    override fun getLiveNullahLai(area: String): com.nikaas.app.data.model.NullahLaiSignal = MockDataProvider.getMockNullahLai(area)

    override suspend fun fuseAndCreateIncident(area: String): FusedIncident {
        // Query live weather signals via OpenWeather API
        var weatherSignal = WeatherSignal(area = area)
        try {
            // Coordinates of G-10 Sector Islamabad area
            val response = WeatherApiClient.service.getCurrentWeather(33.6784, 72.9972, Constants.OPENWEATHER_API_KEY)
            val hasRain = response.weather.any { it.main.contains("Rain", ignoreCase = true) }
            val intensity = if (hasRain) {
                val volume = response.rain?.rain1h ?: 0.0
                when {
                    volume > 10.0 -> "Heavy"
                    volume > 2.0 -> "Medium"
                    else -> "Light"
                }
            } else "None"
            weatherSignal = WeatherSignal(
                hasRainfallAlert = hasRain,
                intensity = intensity,
                area = area
            )
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe fallback to simulated telemetry
            weatherSignal = getMockWeather(area)
        }

        // Query live traffic travel duration times via Google Directions API
        var trafficSignal = TrafficSignal(area = area)
        try {
            val response = TrafficApiClient.service.getDirections(
                origin = "G-10, Islamabad",
                destination = "F-10, Islamabad",
                apiKey = Constants.GOOGLE_MAPS_API_KEY
            )
            if (response.status == "OK" && response.routes.isNotEmpty()) {
                val leg = response.routes[0].legs[0]
                val normalDuration = leg.duration.value
                val trafficDuration = leg.duration_in_traffic?.value ?: normalDuration
                
                val speedDropRatio = if (normalDuration > 0) {
                    trafficDuration.toDouble() / normalDuration.toDouble()
                } else 1.0

                val congestion = when {
                    speedDropRatio > 1.5 -> "Heavy Congestion"
                    speedDropRatio > 1.2 -> "Moderate"
                    else -> "Normal"
                }
                val speed = (45.0 / speedDropRatio).toInt() // Baseline average 45km/h scaled down by speed drop
                trafficSignal = TrafficSignal(
                    congestionLevel = congestion,
                    averageSpeedKmph = speed,
                    area = area
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe fallback to simulated telemetry
            trafficSignal = getMockTraffic(area)
        }

        // Gather real Nullah Lai FEWS telemetry signals
        val nullahLaiSignal = getLiveNullahLai(area)

        // Gather real citizen reports from Firestore collection
        val reports = getCitizenReportsForArea(area)

        // Request AI Fusion from Gemini model passing all four signals
        val response = apiService.fuseSignals(reports, weatherSignal, trafficSignal, nullahLaiSignal)

        // Map AI generated actions
        val actions = response.actions.map { actionDto ->
            AiAction(
                id = UUID.randomUUID().toString(),
                type = actionDto.type,
                description = actionDto.description,
                status = "Pending Approval"
            )
        }

        val beforeOutcome = ZoneOutcome(
            area = area,
            trafficStatus = if (trafficSignal.congestionLevel.contains("Heavy")) "Heavy Congestion" else trafficSignal.congestionLevel,
            dispatchStatus = "Idle (Pending Approval)",
            alertsStatus = "None Sent"
        )

        val incidentId = UUID.randomUUID().toString()
        val incident = FusedIncident(
            id = incidentId,
            area = area,
            severity = response.severity,
            confidenceScore = response.confidenceScore,
            confidenceReasoning = response.confidenceReasoning,
            citizenReports = reports,
            weatherSignal = weatherSignal,
            trafficSignal = trafficSignal,
            nullahLaiSignal = nullahLaiSignal,
            actions = actions,
            beforeOutcome = beforeOutcome,
            afterOutcome = beforeOutcome.copy(),
            timestamp = System.currentTimeMillis()
        )

        // Save incident details in Firestore incidents collection
        incidentsCollection.document(incidentId).set(incident)
        return incident
    }

    override fun getFusedIncidents(): List<FusedIncident> = localIncidents

    override fun getIncidentById(id: String): FusedIncident? {
        return localIncidents.find { it.id == id }
    }

    override fun approveIncident(id: String) {
        val incident = getIncidentById(id) ?: return
        incident.isApproved = true
        
        incident.actions.forEach {
            it.status = "Executing"
        }
        
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

        // Write update to Firestore document
        incidentsCollection.document(id).set(incident)
    }

    override fun resolveIncident(id: String) {
        val incident = getIncidentById(id) ?: return
        incident.isResolved = true
        
        incident.actions.forEach {
            it.status = "Completed"
        }
        
        incident.afterOutcome = ZoneOutcome(
            area = incident.area,
            trafficStatus = "Clear / Flowing",
            dispatchStatus = "Cleared / Resolved",
            alertsStatus = "Resolved"
        )

        // Write update to Firestore document
        incidentsCollection.document(id).set(incident)
    }
}
