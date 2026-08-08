package com.nikaas.app.ui.authority

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.domain.model.FusedIncident
import com.nikaas.app.domain.usecase.ExecuteActionUseCase
import com.nikaas.app.domain.usecase.FuseSignalsUseCase
import com.nikaas.app.ui.common.UIState
import com.nikaas.app.utils.ServiceLocator
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: NikaasRepository = ServiceLocator.repository,
    private val fuseSignalsUseCase: FuseSignalsUseCase = FuseSignalsUseCase(repository),
    private val executeActionUseCase: ExecuteActionUseCase = ExecuteActionUseCase(repository)
) : ViewModel() {

    private val _incidentState = MutableLiveData<UIState<FusedIncident?>>()
    val incidentState: LiveData<UIState<FusedIncident?>> get() = _incidentState

    init {
        // Initial state is success with null (no incident processed yet)
        _incidentState.value = UIState.Success(null)
    }

    fun getSectors(): List<String> = repository.getSectors()

    fun fuseAndAnalyze(area: String) {
        viewModelScope.launch {
            _incidentState.value = UIState.Loading
            try {
                val incident = fuseSignalsUseCase(area)
                _incidentState.value = UIState.Success(incident)
            } catch (e: Exception) {
                _incidentState.value = UIState.Error(e.message ?: "Failed to fuse signals")
            }
        }
    }

    fun approveResponse(incidentId: String) {
        viewModelScope.launch {
            executeActionUseCase.approveIncident(incidentId)
            // Reload the updated incident from the repository
            val updatedIncident = repository.getIncidentById(incidentId)
            _incidentState.value = UIState.Success(updatedIncident)
        }
    }

    fun resolveIncident(incidentId: String) {
        viewModelScope.launch {
            executeActionUseCase.resolveIncident(incidentId)
            // Reload the updated incident from the repository
            val updatedIncident = repository.getIncidentById(incidentId)
            _incidentState.value = UIState.Success(updatedIncident)
        }
    }
}
