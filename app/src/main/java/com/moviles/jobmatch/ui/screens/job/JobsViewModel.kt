package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.Job
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JobsUiState(
    val jobs: List<Job> = emptyList(),
    val searchText: String = "",
    val selectedCategory: String = "Todos",
    val isLoading: Boolean = false,
    val jobsCount: Int = 0,
    val errorMessage: String? = null
)

class JobsViewModel(private val repository: JobRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JobsUiState())
    val uiState: StateFlow<JobsUiState> = _uiState.asStateFlow()

    private var allJobsFromServer: List<Job> = emptyList()

    init {
        loadJobs()
    }

    fun loadJobs() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = repository.getJobs()) {
                is ApiResult.Success -> {
                    allJobsFromServer = result.data
                    _uiState.value = _uiState.value.copy(
                        jobs = allJobsFromServer,
                        isLoading = false,
                        jobsCount = allJobsFromServer.size
                    )
                }
                is ApiResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
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
        val filtered = allJobsFromServer.filter { job ->
            val matchesSearch = _uiState.value.searchText.isEmpty() ||
                    job.title.contains(_uiState.value.searchText, ignoreCase = true) ||
                    job.idCompany.contains(_uiState.value.searchText, ignoreCase = true)

            val matchesCategory = _uiState.value.selectedCategory == "Todos" ||
                    when (_uiState.value.selectedCategory) {
                        "Eventos" -> job.title.contains("Event", ignoreCase = true)
                        "Logística" -> job.title.contains("Delivery", ignoreCase = true) || job.title.contains("Repartidor", ignoreCase = true)
                        "Oficina" -> job.title.contains("Administrative", ignoreCase = true) || job.title.contains("Apoyo", ignoreCase = true)
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