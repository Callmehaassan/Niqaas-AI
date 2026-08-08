package com.nikaas.app.domain.usecase

import com.nikaas.app.data.repository.NikaasRepository

class ExecuteActionUseCase(private val repository: NikaasRepository) {
    
    fun approveIncident(incidentId: String) {
        repository.approveIncident(incidentId)
    }

    fun resolveIncident(incidentId: String) {
        repository.resolveIncident(incidentId)
    }
}
