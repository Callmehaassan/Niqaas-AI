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
        traffic: TrafficSignal
    ): FusionResponseDto = withContext(Dispatchers.IO) {
        val apiKey = Constants.GEMINI_API_KEY
        if (apiKey.isBlank()) {
            return@withContext performFallbackFusion(reports, weather, traffic)
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
                You are a flood response coordinator AI for a system called "Nikaas" (Urdu for drainage) in Islamabad, Pakistan.
                Fuse the following inputs for the area '${weather.area}':
                
                Citizen Reports:
                ${reports.joinToString("\n") { "- '${it.description}' (timestamp: ${it.timestamp})" }}
                
                Weather Signal:
                - Has active rainfall alert: ${weather.hasRainfallAlert}
                - Rain intensity: ${weather.intensity}
                
                Traffic Signal:
                - Congestion level: ${traffic.congestionLevel}
                - Average speed: ${traffic.averageSpeedKmph} km/h
                
                Analyze the inputs and do the following:
                1. Judge severity as 'Low', 'Medium', or 'High' by fusing all three signals (do not rely on citizen reports alone).
                2. Assign a confidence score (between 0 and 100) and clear, human-readable reasoning explaining why you decided this severity (e.g., "High confidence: 2 citizen reports + active rainfall alert + traffic congestion spike in G-10 within the last 20 minutes").
                3. Recommend specific, localized coordinated actions based on severity. The action types MUST be one of: 'REROUTE', 'DISPATCH', 'ALERT'. Make the action descriptions extremely realistic for Islamabad (e.g. mention WASA teams, specific alternate streets, Rescue 1122, etc.).
                
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
            performFallbackFusion(reports, weather, traffic)
        }
    }

    private fun performFallbackFusion(
        reports: List<CitizenReport>,
        weather: WeatherSignal,
        traffic: TrafficSignal
    ): FusionResponseDto {
        val reportCount = reports.size
        val hasRain = weather.hasRainfallAlert
        val isCongested = traffic.congestionLevel.contains("Heavy") || traffic.congestionLevel.contains("Moderate")

        val (info, actions) = when {
            reportCount >= 2 && hasRain && isCongested -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("REROUTE", "Reroute traffic from ${weather.area} via Service Road West or nearest arterial roads due to flash flooding."),
                    FusionResponseDto.ActionDto("DISPATCH", "Dispatch WASA Emergency Municipal Drain Cleansing Team to clear nullah blockage in ${weather.area}."),
                    FusionResponseDto.ActionDto("ALERT", "Send SMS emergency alert to residents in ${weather.area}: Underpass flooded, seek alternate routes immediately.")
                )
                Triple("High", 95, "High confidence: $reportCount citizen reports + active rainfall alert + traffic congestion spike in ${weather.area} within the last 20 minutes.") to act
            }
            reportCount >= 1 && (hasRain || isCongested) -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("DISPATCH", "Dispatch WASA team to inspect nullah water levels in ${weather.area}."),
                    FusionResponseDto.ActionDto("ALERT", "Alert nearby residents to exercise caution in ${weather.area} due to rising water levels.")
                )
                Triple("Medium", 75, "Medium confidence: $reportCount citizen reports + weather/traffic activity in ${weather.area}.") to act
            }
            else -> {
                val act = listOf(
                    FusionResponseDto.ActionDto("ALERT", "Monitor ${weather.area} water levels. No active emergency response required.")
                )
                Triple("Low", 45, "Low confidence: Minimal citizen reports and normal traffic/weather indicators in ${weather.area}.") to act
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
