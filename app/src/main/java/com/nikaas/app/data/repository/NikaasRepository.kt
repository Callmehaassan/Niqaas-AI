package com.nikaas.app.data.repository

import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.domain.model.FusedIncident

interface NikaasRepository {
    fun getSectors(): List<String>
    fun getCitizenReports(): List<CitizenReport>
    fun getCitizenReportsForArea(area: String): List<CitizenReport>
    fun submitCitizenReport(report: CitizenReport, imageBitmap: android.graphics.Bitmap?)
    
    fun getMockWeather(area: String): WeatherSignal
    fun getMockTraffic(area: String): TrafficSignal
    fun getLiveNullahLai(area: String, weather: WeatherSignal): com.nikaas.app.data.model.NullahLaiSignal
    
    suspend fun fuseAndCreateIncident(area: String): FusedIncident
    fun getFusedIncidents(): List<FusedIncident>
    fun getIncidentById(id: String): FusedIncident?
    
    fun approveIncident(id: String)
    fun resolveIncident(id: String)
}
