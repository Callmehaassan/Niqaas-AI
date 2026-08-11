package com.nikaas.app.ui.citizen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nikaas.app.data.model.CitizenReport
import com.nikaas.app.data.repository.NikaasRepository
import com.nikaas.app.utils.ServiceLocator

class CitizenReportViewModel(
    private val repository: NikaasRepository = ServiceLocator.repository
) : ViewModel() {

    private val _reports = MutableLiveData<List<CitizenReport>>()
    val reports: LiveData<List<CitizenReport>> get() = _reports

    init {
        loadReports()
    }

    fun loadReports() {
        _reports.value = repository.getCitizenReports()
    }

    fun submitReport(location: String, description: String, imageBitmap: android.graphics.Bitmap?) {
        val report = CitizenReport(
            location = location,
            description = description,
            timestamp = System.currentTimeMillis()
        )
        repository.submitCitizenReport(report, imageBitmap)
        loadReports()
    }

    fun getSectors(): List<String> {
        return repository.getSectors()
    }
}
