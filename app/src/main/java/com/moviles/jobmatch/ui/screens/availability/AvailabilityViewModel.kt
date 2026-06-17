package com.moviles.jobmatch.ui.screens.availability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.TimeSlot
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimeBlock(val label: String, val range: String, val start: String, val end: String) {
    MORNING("Mañana", "6am - 12pm", "06:00", "12:00"),
    AFTERNOON("Tarde", "12pm - 6pm", "12:00", "18:00"),
    NIGHT("Noche", "6pm - 12am", "18:00", "00:00")
}


data class AvailabilityUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    // grid[dayIndex][blockIndex] = true/false
    val grid: List<List<Boolean>> = List(7) { List(3) { false } },

    val blockActiveDays: List<Int> = List(3) { 0 }
)

class AvailabilityViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AvailabilityUiState())
    val uiState: StateFlow<AvailabilityUiState> = _uiState.asStateFlow()

    init {
        loadAvailability()
    }

    fun loadAvailability() {
        val studentId = AuthSession.currentUser?.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = studentRepository.getStudentProfile(studentId)) {
                is ApiResult.Success -> {
                    val avail = result.data.availability ?: AvailabilityResponse()
                    val grid = availabilityResponseToGrid(avail)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            grid = grid,
                            blockActiveDays = computeBlockActiveDays(grid)
                        )
                    }
                }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun toggleCell(dayIndex: Int, blockIndex: Int) {
        val current = _uiState.value.grid
        val newGrid = current.mapIndexed { d, row ->
            if (d == dayIndex) row.mapIndexed { b, v -> if (b == blockIndex) !v else v }
            else row
        }
        _uiState.update {
            it.copy(
                grid = newGrid,
                blockActiveDays = computeBlockActiveDays(newGrid)
            )
        }
    }

    fun saveAvailability() {
        val studentId = AuthSession.currentUser?.userId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val body = gridToAvailabilityResponse(_uiState.value.grid)
            when (val result = studentRepository.updateAvailability(studentId, body)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
            }
        }
    }

    // ---------- helpers ----------

    private fun availabilityResponseToGrid(a: AvailabilityResponse): List<List<Boolean>> {
        val days = listOf(a.monday, a.tuesday, a.wednesday, a.thursday, a.friday, a.saturday, a.sunday)
        return days.map { slots ->
            TimeBlock.entries.map { block ->
                slots.any { it.start == block.start }
            }
        }
    }

    private fun gridToAvailabilityResponse(grid: List<List<Boolean>>): AvailabilityResponse {
        fun slotsFor(dayIndex: Int): List<TimeSlot> =
            TimeBlock.entries.mapIndexedNotNull { bi, block ->
                if (grid[dayIndex][bi]) TimeSlot(block.start, block.end) else null
            }
        return AvailabilityResponse(
            monday    = slotsFor(0),
            tuesday   = slotsFor(1),
            wednesday = slotsFor(2),
            thursday  = slotsFor(3),
            friday    = slotsFor(4),
            saturday  = slotsFor(5),
            sunday    = slotsFor(6)
        )
    }

    private fun computeBlockActiveDays(grid: List<List<Boolean>>): List<Int> =
        List(3) { bi -> grid.count { row -> row[bi] } }
}

class AvailabilityViewModelFactory(
    private val studentRepository: StudentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AvailabilityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AvailabilityViewModel(studentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}