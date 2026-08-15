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

    /**
     * Simulates weather signals for the selected sector.
     */
    fun getMockWeather(area: String): WeatherSignal {
        val cleanArea = area.trim()
        return when {
            cleanArea.contains("G-10") -> WeatherSignal(hasRainfallAlert = true, intensity = "Heavy", area = area, temp = 24.5, description = "Heavy Rain")
            cleanArea.contains("F-8") -> WeatherSignal(hasRainfallAlert = true, intensity = "Medium", area = area, temp = 26.0, description = "Showers")
            cleanArea.contains("I-8") -> WeatherSignal(hasRainfallAlert = false, intensity = "Light", area = area, temp = 28.2, description = "Drizzle")
            cleanArea.contains("E-11") -> WeatherSignal(hasRainfallAlert = true, intensity = "Heavy", area = area, temp = 23.8, description = "Stormy")
            else -> WeatherSignal(hasRainfallAlert = false, intensity = "None", area = area, temp = 30.5, description = "Clear Sky")
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
     * Simulates live Nullah Lai hydrology sensor levels based on region and live weather rainfall rates.
     */
    fun getMockNullahLai(area: String, weather: WeatherSignal): com.nikaas.app.data.model.NullahLaiSignal {
        val cleanArea = area.trim()
        val baseSignal = when {
            cleanArea.contains("G-10") || cleanArea.contains("E-11") -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 13.2, // Warning baseline
                    gawalmandiWaterLevelFt = 14.5,
                    catchmentRainfallMm = 45.0,
                    status = "Warning"
                )
            }
            cleanArea.contains("F-8") -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 9.8,
                    gawalmandiWaterLevelFt = 8.4,
                    catchmentRainfallMm = 20.0,
                    status = "Alert"
                )
            }
            else -> {
                com.nikaas.app.data.model.NullahLaiSignal(
                    kattarianWaterLevelFt = 4.2,
                    gawalmandiWaterLevelFt = 3.5,
                    catchmentRainfallMm = 0.0,
                    status = "Normal"
                )
            }
        }

        // Add real-time rainfall rate adjustments from OpenWeather API!
        val additionalRain = if (weather.hasRainfallAlert) {
            when (weather.intensity) {
                "Heavy" -> 6.5
                "Medium" -> 3.2
                "Light" -> 1.5
                else -> 0.0
            }
        } else 0.0

        val finalKattarian = baseSignal.kattarianWaterLevelFt + additionalRain
        val finalGawalmandi = baseSignal.gawalmandiWaterLevelFt + additionalRain
        val finalRainfall = baseSignal.catchmentRainfallMm + (additionalRain * 4.0)

        val finalStatus = when {
            finalGawalmandi >= 15.0 || finalKattarian >= 15.0 -> "Danger"
            finalGawalmandi >= 11.5 || finalKattarian >= 11.5 -> "Warning"
            finalGawalmandi >= 8.0 || finalKattarian >= 8.0 -> "Alert"
            else -> "Normal"
        }

        return com.nikaas.app.data.model.NullahLaiSignal(
            kattarianWaterLevelFt = finalKattarian,
            gawalmandiWaterLevelFt = finalGawalmandi,
            catchmentRainfallMm = finalRainfall,
            status = finalStatus
        )
    }
}
