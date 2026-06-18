package com.moviles.jobmatch.ui.screens.job

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.data.remote.model.UpdateJobRequest
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.AppContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class EditJobUiState {
    object Idle : EditJobUiState()
    object Loading : EditJobUiState()
    data class JobLoaded(val job: JobDetailResponse) : EditJobUiState()
    object SaveSuccess : EditJobUiState()
    data class Error(val message: String) : EditJobUiState()
}

data class EditFormState(
    val title: String = "",
    val description: String = "",
    val payment: String = "",
    val paymentType: String = "Hora",
    val workDate: String = "",
    val startTime: String = "",
    val endTime: String = "",
    val skills: List<String> = emptyList(),
    val skillInput: String = "",
    val errorMessage: String? = null
)

class EditJobViewModel : ViewModel() {
    private val repository = AppContainer.jobRepository

    private val _uiState = MutableStateFlow<EditJobUiState>(EditJobUiState.Idle)
    val uiState: StateFlow<EditJobUiState> = _uiState.asStateFlow()

    private val _formState = MutableStateFlow(EditFormState())
    val formState: StateFlow<EditFormState> = _formState.asStateFlow()

    fun loadJob(jobId: Int) {
        viewModelScope.launch {
            _uiState.value = EditJobUiState.Loading
            when (val result = repository.getJobById(jobId)) {
                is ApiResult.Success -> {
                    val job = result.data
                    _formState.value = EditFormState(
                        title = job.title,
                        description = job.description,
                        payment = job.payment.toBigDecimal().stripTrailingZeros().toPlainString(),
                        paymentType = when (job.paymentType.lowercase()) {
                            "hora", "por hora" -> "Hora"
                            "turno" -> "Turno"
                            "proyecto" -> "Proyecto"
                            else -> job.paymentType
                        },
                        workDate = job.workDate.take(10),
                        startTime = job.startTime.take(5),
                        endTime = job.endTime.take(5),
                        skills = job.deliverables
                            ?.split(",")
                            ?.map { it.trim() }
                            ?.filter { it.isNotEmpty() }
                            ?: emptyList()
                    )
                    _uiState.value = EditJobUiState.JobLoaded(job)
                }
                is ApiResult.Error -> _uiState.value = EditJobUiState.Error(result.message)
            }
        }
    }

    fun updateTitle(value: String) = _formState.update { it.copy(title = value, errorMessage = null) }
    fun updateDescription(value: String) = _formState.update { it.copy(description = value, errorMessage = null) }
    fun updatePayment(value: String) = _formState.update { it.copy(payment = value, errorMessage = null) }
    fun updatePaymentType(value: String) = _formState.update { it.copy(paymentType = value) }
    fun updateWorkDate(value: String) = _formState.update { it.copy(workDate = value, errorMessage = null) }
    fun updateStartTime(value: String) = _formState.update { it.copy(startTime = value, errorMessage = null) }
    fun updateEndTime(value: String) = _formState.update { it.copy(endTime = value, errorMessage = null) }
    fun updateSkillInput(value: String) = _formState.update { it.copy(skillInput = value) }

    fun addSkill() {
        val input = _formState.value.skillInput.trim()
        if (input.isNotBlank()) {
            _formState.update { it.copy(skills = it.skills + input, skillInput = "") }
        }
    }

    fun removeSkill(index: Int) {
        _formState.update { it.copy(skills = it.skills.toMutableList().also { list -> list.removeAt(index) }) }
    }

    fun saveJob(jobId: Int) {
        val form = _formState.value
        val amount = form.payment.toDoubleOrNull()
        val validationError = when {
            form.title.isBlank() -> "El título es requerido"
            form.description.isBlank() -> "La descripción es requerida"
            form.workDate.isBlank() -> "La fecha es requerida"
            form.startTime.isBlank() -> "La hora de inicio es requerida"
            form.endTime.isBlank() -> "La hora de fin es requerida"
            form.payment.isBlank() -> "El monto es requerido"
            amount == null -> "El monto debe ser un número válido"
            else -> null
        }
        if (validationError != null) {
            _formState.update { it.copy(errorMessage = validationError) }
            return
        }
        viewModelScope.launch {
            _uiState.value = EditJobUiState.Loading
            val request = UpdateJobRequest(
                title = form.title,
                description = form.description,
                payment = amount!!,
                paymentType = form.paymentType.lowercase(),
                workDate = form.workDate,
                startTime = "${form.startTime}:00",
                endTime = "${form.endTime}:00",
                deliverables = form.skills.ifEmpty { null }
            )
            when (val result = repository.updateJob(jobId, request)) {
                is ApiResult.Success -> _uiState.value = EditJobUiState.SaveSuccess
                is ApiResult.Error -> {
                    _uiState.value = EditJobUiState.Idle
                    _formState.update { it.copy(errorMessage = result.message) }
                }
            }
        }
    }
}
