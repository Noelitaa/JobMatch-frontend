package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.moviles.jobmatch.data.Job

data class JobsUiState(
    val jobs: List<Job> = emptyList(),
    val searchText: String = "",
    val selectedCategory: String = "All",
    val isLoading: Boolean = false,
    val jobsCount: Int = 0
)

class JobsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    private val allJobs = listOf(
        Job(
            id = 1,
            title = "Event Assistant (Weekend)",
            company = "Producciones Atlas",
            fitPercentage = 95,
            schedule = "6h daily",
            distanceKm = 1.2,
            paymentType = "Hourly payment",
            amount = 2500
        ),
        Job(
            id = 2,
            title = "University Delivery Driver",
            company = "QuickDrop CR",
            fitPercentage = 88,
            schedule = "Flexible",
            distanceKm = 0.5,
            paymentType = "Hourly payment",
            amount = 1800
        ),
        Job(
            id = 3,
            title = "Administrative Support",
            company = "Bufete Solano",
            fitPercentage = 72,
            schedule = "4h daily",
            distanceKm = 3.5,
            paymentType = "Hourly payment",
            amount = 1800
        ),
        Job(
            id = 4,
            title = "Tech Brand Promoter",
            company = "Innovación Digital",
            fitPercentage = 91,
            schedule = "8h (Saturday)",
            distanceKm = 2.1,
            paymentType = "Hourly payment",
            amount = 2800
        )
    )

    init {
        loadJobs()
    }

    private fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(500)
            _uiState.value = _uiState.value.copy(
                jobs = allJobs,
                isLoading = false,
                jobsCount = allJobs.size
            )
        }
    }

    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(searchText = text)
        filterJobs()
    }

    fun updateSelectedCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        filterJobs()
    }

    private fun filterJobs() {
        val filtered = allJobs.filter { job ->
            val matchesSearch = _uiState.value.searchText.isEmpty() ||
                    job.title.contains(_uiState.value.searchText, ignoreCase = true) ||
                    job.company.contains(_uiState.value.searchText, ignoreCase = true)

            val matchesCategory = _uiState.value.selectedCategory == "All" ||
                    when (_uiState.value.selectedCategory) {
                        "Events" -> job.title.contains("Event", ignoreCase = true)
                        "Logistics" -> job.title.contains("Delivery", ignoreCase = true)
                        "Office" -> job.title.contains("Administrative", ignoreCase = true)
                        else -> true
                    }

            matchesSearch && matchesCategory
        }

        _uiState.value = _uiState.value.copy(
            jobs = filtered,
            jobsCount = filtered.size
        )
    }
}