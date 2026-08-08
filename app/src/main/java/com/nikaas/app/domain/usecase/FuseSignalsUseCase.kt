package com.nikaas.app.domain.usecase

import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.domain.model.FusedIncident

class FuseSignalsUseCase(private val repository: NikaasRepository) {
    suspend operator fun invoke(area: String): FusedIncident {
        return repository.fuseAndCreateIncident(area)
    }
}
