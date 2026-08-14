package com.nikaas.app.ui.citizen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.utils.ServiceLocator

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CitizenReportViewModel(
    private val repository: NikaasRepository = ServiceLocator.repository
) : ViewModel() {

    private val _reports = MutableLiveData<List<CitizenReport>>()
    val reports: LiveData<List<CitizenReport>> get() = _reports

    private val _activeWarning = MutableLiveData<FusedIncident?>()
    val activeWarning: LiveData<FusedIncident?> get() = _activeWarning

    init {
        loadReports()
        checkForActiveWarnings()
    }

    fun checkForActiveWarnings() {
        viewModelScope.launch {
            val incidents = repository.getFusedIncidents()
            val warning = incidents.firstOrNull { it.isApproved && !it.isResolved && (it.severity == "High" || it.severity == "Medium") }
            _activeWarning.value = warning
        }
    }

    fun loadReports() {
        _reports.value = repository.getCitizenReports()
    }

    fun submitReport(
        location: String,
        description: String,
        imageBitmap: android.graphics.Bitmap?,
        reporterUid: String,
        reporterName: String,
        onCompleted: () -> Unit
    ) {
        viewModelScope.launch {
            var blockageType = "None"
            var blockageSeverity = "Low"

            if (imageBitmap != null) {
                try {
                    val apiService = com.nikaas.app.data.api.GeminiApiService()
                    val result = apiService.classifyBlockageImage(imageBitmap)
                    blockageType = result.blockageType ?: "None"
                    blockageSeverity = result.blockageSeverity ?: "Low"
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            val report = CitizenReport(
                location = location,
                description = description,
                blockageType = blockageType,
                blockageSeverity = blockageSeverity,
                reporterUid = reporterUid,
                reporterName = reporterName,
                timestamp = System.currentTimeMillis()
            )
            repository.submitCitizenReport(report, imageBitmap)
            loadReports()
            onCompleted()
        }
    }

    fun getSectors(): List<String> {
        return repository.getSectors()
    }
}
