package com.nikaas.app.data.api

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.gson.Gson
import com.nikaas.app.data.api.dto.FusionResponseDto
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.model.TrafficSignal
import com.nikaas.app.data.model.WeatherSignal
import com.nikaas.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiApiService {

    private val gson = Gson()

    /**
     * Fuses multi-source signals using Gemini AI to judge severity, confidence, and recommended actions.
     */
    suspend fun fuseSignals(
        reports: List<CitizenReport>,
        weather: WeatherSignal,
        traffic: TrafficSignal,
        nullahLai: com.nikaas.app.data.model.NullahLaiSignal
    ): FusionResponseDto = withContext(Dispatchers.IO) {
        val apiKey = Constants.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext performFallbackFusion(reports, weather, traffic, nullahLai)
        }

        try {
            val config = generationConfig {
                responseMimeType = "application/json"
            }

            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = apiKey,
                generationConfig = config
            )

            val prompt = """
                You are a flood response coordinator AI for a system called "Nikaas" (Urdu for drainage) in Islamabad/Rawalpindi, Pakistan.
                You are the upper intelligence layer operating on top of the Nullah Lai Flood Early Warning System (FEWS).
                Fuse the following multi-source inputs for '${weather.area}':
                
                Citizen Reports:
                ${reports.joinToString("\n") { "- '${it.description}' (timestamp: ${it.timestamp})" }}
                
                Weather Signal:
                - Has active rainfall alert: ${weather.hasRainfallAlert}
                - Rain intensity: ${weather.intensity}
                
                Traffic Signal:
                - Congestion level: ${traffic.congestionLevel}
                - Average speed: ${traffic.averageSpeedKmph} km/h
                
                Nullah Lai FEWS Telemetry Stream:
                - Kattarian Bridge Water Level: ${nullahLai.kattarianWaterLevelFt} ft
                - Gawalmandi Bridge Water Level: ${nullahLai.gawalmandiWaterLevelFt} ft
                - Catchment Rainfall Gauge (Margalla): ${nullahLai.catchmentRainfallMm} mm
                - Hydrology Alert Status: ${nullahLai.status}
                
                Analyze the inputs and do the following:
                1. Judge severity as 'Low', 'Medium', or 'High' by fusing all four signals (give special weight to Nullah Lai FEWS Danger levels or matching Citizen Reports).
                2. Assign a confidence score (between 0 and 100) and clear, human-readable reasoning explaining why you decided this severity (e.g., "High confidence: 2 citizen reports + active rainfall alert + Nullah Lai water level Warning at Kattarian [16.2 ft]").
                3. Recommend specific, localized coordinated actions based on severity. The action types MUST be one of: 'REROUTE', 'DISPATCH', 'ALERT'. Make the action descriptions extremely realistic for Islamabad/Rawalpindi (e.g. mention WASA team deployments at Kattarian/Gawalmandi, specific alternate streets, Rescue 1122, alert SMS warnings to nearby low-lying residents, etc.).
                
                Format your response strictly as a single JSON object matching the following structure:
                {
                  "severity": "Low" | "Medium" | "High",
                  "confidenceScore": 85,
                  "confidenceReasoning": "Human-readable reasoning here...",
                  "actions": [
                    {
                      "type": "REROUTE" | "DISPATCH" | "ALERT",
                      "description": "Action description here..."
                    }
                  ]
                }
            """.trimIndent()

            val response = generativeModel.generateContent(prompt)
            val jsonText = response.text ?: throw Exception("Empty response from Gemini")
            
            // Strip markdown block tags if they exist
            val cleanJson = jsonText.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()

            gson.fromJson(cleanJson, FusionResponseDto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            performFallbackFusion(reports, weather, traffic, nullahLai)
        }
    }

    private fun performFallbackFusion(
        reports: List<CitizenReport>,
        weather: WeatherSignal,
        traffic: TrafficSignal,
        nullahLai: com.nikaas.app.data.model.NullahLaiSignal
    ): FusionResponseDto {
        val reportCount = reports.size
        val hasRain = weather.hasRainfallAlert
        val isCongested = traffic.congestionLevel.contains("Heavy") || traffic.congestionLevel.contains("Moderate")
        val isNullahLaiCritical = nullahLai.status == "Danger" || nullahLai.status == "Warning"

        val (info, actions) = when {
            (reportCount >= 2 && hasRain && isCongested) || isNullahLaiCritical -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("REROUTE", "Reroute traffic from ${weather.area} via Service Road West or nearest arterial roads due to flash flooding and rising Nullah Lai levels."),
                    FusionResponseDto.ActionDto("DISPATCH", "Dispatch WASA Emergency Municipal Drain Cleansing Team to Gawalmandi/Kattarian bridges and clear municipal blocks in ${weather.area}."),
                    FusionResponseDto.ActionDto("ALERT", "Send SMS emergency alert to residents in low-lying areas near ${weather.area}: Nullah Lai water level at ${nullahLai.gawalmandiWaterLevelFt} ft, prepare for evacuation.")
                )
                Triple("High", 95, "High confidence: Nullah Lai FEWS Alert level reached (${nullahLai.kattarianWaterLevelFt} ft / ${nullahLai.status}) + weather rain advisories in ${weather.area}.") to act
            }
            reportCount >= 1 && (hasRain || isCongested) -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("DISPATCH", "Dispatch WASA team to inspect Nullah Lai water levels at Kattarian Bridge."),
                    FusionResponseDto.ActionDto("ALERT", "Alert nearby residents to exercise caution in ${weather.area} due to rising catchment runoff.")
                )
                Triple("Medium", 75, "Medium confidence: $reportCount citizen reports + rising water indicators in ${weather.area}.") to act
            }
            else -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("ALERT", "Monitor ${weather.area} Nullah Lai water levels. No active emergency response required.")
                )
                Triple("Low", 45, "Low confidence: Minimal citizen reports and normal Nullah Lai FEWS water levels (${nullahLai.kattarianWaterLevelFt} ft).") to act
            }
        }

        val (severity, score, reasoning) = info

        return FusionResponseDto(
            severity = severity,
            confidenceScore = score,
            confidenceReasoning = reasoning,
            actions = actions.map { FusionResponseDto.ActionDto(it.type, it.description) }
        )
    }
}
