package com.nikaas.app.ui.citizen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.domain.model.FusedIncident
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

    private val _weatherSignal = MutableLiveData<com.nikaas.app.data.model.WeatherSignal>()
    val weatherSignal: LiveData<com.nikaas.app.data.model.WeatherSignal> get() = _weatherSignal

    init {
        loadReports()
        checkForActiveWarnings()
        loadWeather("G-10 Sector")
    }

    fun loadWeather(area: String) {
        viewModelScope.launch {
            try {
                val weather = repository.getMockWeather(area)
                _weatherSignal.postValue(weather)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
