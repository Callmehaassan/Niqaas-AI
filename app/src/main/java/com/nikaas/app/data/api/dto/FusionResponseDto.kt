package com.nikaas.app.data.api.dto

data class FusionResponseDto(
    val severity: String = "Low",
    val confidenceScore: Int = 0,
    val confidenceReasoning: String = "",
    val urduAlert: String = "",
    val englishAlert: String = "",
    val actions: List<ActionDto> = emptyList()
) {
    data class ActionDto(
        val type: String = "",
        val description: String = ""
    )
}
