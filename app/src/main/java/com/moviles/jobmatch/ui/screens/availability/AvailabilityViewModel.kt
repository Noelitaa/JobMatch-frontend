package com.moviles.jobmatch.ui.screens.availability

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.AvailabilityResponse
import com.moviles.jobmatch.data.remote.model.TimeBlockRequest
import com.moviles.jobmatch.data.remote.model.UpdateAvailabilityRequest
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Fixed business time blocks
// IMPORTANT: backend deserializes these as System.TimeOnly, which requires
// "HH:mm:ss" format. A plain "00:00" caused 400 errors — and "00:00" as an
// END time would also mean midnight wrapping to the START of the day, which
// TimeOnly cannot represent as an end-of-day value. Using 23:59:59 instead.
enum class TimeBlock(val label: String, val range: String, val start: String, val end: String) {
    MORNING("Mañana", "6am - 12pm", "06:00:00", "12:00:00"),
    AFTERNOON("Tarde", "12pm - 6pm", "12:00:00", "18:00:00"),
    NIGHT("Noche", "6pm - 12am", "18:00:00", "23:59:59")
}

// Day indices: 0=Mon … 6=Sun
data class AvailabilityUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,   // load error — replaces full screen
    val saveError: String? = null,      // save error — shown inline below button
    // grid[dayIndex][blockIndex] = true/false
    val grid: List<List<Boolean>> = List(7) { List(3) { false } },
    // how many active days each block has
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
            _uiState.update { it.copy(isSaving = true, saveError = null) }

            val body = gridToUpdateRequest(_uiState.value.grid)
            when (val result = studentRepository.updateAvailability(studentId, body)) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isSaving = false, saveError = result.message) }
            }
        }
    }

    // ---------- helpers ----------

    // Maps the GET response (day-keyed object) into the 7x3 boolean grid used by the UI.
    // Comparison is normalized to HH:mm because the GET endpoint's TimeOnly serialization
    // format isn't guaranteed to match the "HH:mm:ss" we send on PUT (e.g. could come back
    // as "06:00:00" or "06:00" depending on backend JSON options).
    private fun availabilityResponseToGrid(a: AvailabilityResponse): List<List<Boolean>> {
        val days = listOf(a.monday, a.tuesday, a.wednesday, a.thursday, a.friday, a.saturday, a.sunday)
        return days.map { slots ->
            TimeBlock.entries.map { block ->
                slots.any { normalizeToHm(it.start) == normalizeToHm(block.start) }
            }
        }
    }

    // Backend PUT contract expects a FLAT list of time blocks, not the day-keyed
    // object the GET endpoint returns. day: 0=Monday … 6=Sunday (matches grid index).
    private fun gridToUpdateRequest(grid: List<List<Boolean>>): UpdateAvailabilityRequest {
        val blocks = mutableListOf<TimeBlockRequest>()
        grid.forEachIndexed { dayIndex, row ->
            val backendDay = (dayIndex + 1) % 7
            TimeBlock.entries.forEachIndexed { blockIndex, block ->
                if (row[blockIndex]) {
                    blocks.add(TimeBlockRequest(day = backendDay, startTime = block.start, endTime = block.end))
                }
            }
        }
        return UpdateAvailabilityRequest(timeBlocks = blocks)
    }

    // Strips seconds so "06:00:00" and "06:00" compare as equal
    private fun normalizeToHm(time: String): String = time.take(5)

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