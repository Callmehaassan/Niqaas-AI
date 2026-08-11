package com.nikaas.app.data.local

import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.domain.model.FusedIncident

object MockDataProvider {

    // Islamabad Sectors used in mock data
    val SECTORS = listOf("G-10 Sector", "F-8 Sector", "I-8 Sector", "E-11 Sector", "Blue Area")

    // In-memory data storage
    val activeCitizenReports = mutableListOf<CitizenReport>()
    val fusedIncidents = mutableListOf<FusedIncident>()

    init {
        // Pre-seed with some initial reports to make the app feel populated on start
        val now = System.currentTimeMillis()
        activeCitizenReports.add(
            CitizenReport(
                id = "1",
                location = "G-10 Sector",
                description = "pani bhar gaya hai, underpass band hai!",
                timestamp = now - 600000 // 10 mins ago
            )
        )
        activeCitizenReports.add(
            CitizenReport(
                id = "2",
                location = "G-10 Sector",
                description = "gaari phans gayi hai pani mein. please help",
                timestamp = now - 300000 // 5 mins ago
            )
        )
        activeCitizenReports.add(
            CitizenReport(
                id = "3",
                location = "F-8 Sector",
                description = "heavy rainfall near nullah, water level rising",
                timestamp = now - 1200000 // 20 mins ago
            )
        )
    }

    /**
     * Simulates weather signals for the selected sector.
     */
    fun getMockWeather(area: String): WeatherSignal {
        val cleanArea = area.trim()
        return when {
            cleanArea.contains("G-10") -> WeatherSignal(hasRainfallAlert = true, intensity = "Heavy", area = area)
            cleanArea.contains("F-8") -> WeatherSignal(hasRainfallAlert = true, intensity = "Medium", area = area)
            cleanArea.contains("I-8") -> WeatherSignal(hasRainfallAlert = false, intensity = "Light", area = area)
            cleanArea.contains("E-11") -> WeatherSignal(hasRainfallAlert = true, intensity = "Heavy", area = area)
            else -> WeatherSignal(hasRainfallAlert = false, intensity = "None", area = area)
        }
    }

    /**
     * Simulates traffic congestion signals for the selected sector.
     */
    fun getMockTraffic(area: String): TrafficSignal {
        val cleanArea = area.trim()
        return when {
            cleanArea.contains("G-10") -> TrafficSignal(congestionLevel = "Heavy Congestion", averageSpeedKmph = 8, area = area)
            cleanArea.contains("F-8") -> TrafficSignal(congestionLevel = "Moderate", averageSpeedKmph = 20, area = area)
            cleanArea.contains("I-8") -> TrafficSignal(congestionLevel = "Normal", averageSpeedKmph = 40, area = area)
            cleanArea.contains("E-11") -> TrafficSignal(congestionLevel = "Heavy Congestion", averageSpeedKmph = 5, area = area)
            else -> TrafficSignal(congestionLevel = "Normal", averageSpeedKmph = 50, area = area)
        }
    }

    /**
     * Simulates live Nullah Lai hydrology sensor levels based on region and rain alerts.
     */
    fun getMockNullahLai(area: String): com.nikaas.app.data.model.NullahLaiSignal {
        val cleanArea = area.trim()
        return when {
            cleanArea.contains("G-10") || cleanArea.contains("E-11") -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 16.2, // WARNING (alert > 11.5, danger > 15)
                    gawalmandiWaterLevelFt = 18.5, // DANGER / EVACUATION (danger > 15, evacuation > 20)
                    catchmentRainfallMm = 52.0,
                    status = "Danger"
                )
            }
            cleanArea.contains("F-8") -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 12.8,
                    gawalmandiWaterLevelFt = 11.4,
                    catchmentRainfallMm = 28.0,
                    status = "Warning"
                )
            }
            cleanArea.contains("I-8") -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 10.2,
                    gawalmandiWaterLevelFt = 9.5,
                    catchmentRainfallMm = 14.0,
                    status = "Alert"
                )
            }
            else -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 5.4,
                    gawalmandiWaterLevelFt = 4.2,
                    catchmentRainfallMm = 0.0,
                    status = "Normal"
                )
            }
        }
    }
}
