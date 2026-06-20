package com.moviles.jobmatch.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.remote.model.StudentSkillResponse
import com.moviles.jobmatch.data.repository.ApiResult
import com.moviles.jobmatch.data.repository.SkillRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SkillSelectionUiState(
    val isLoading: Boolean = false,
    val skills: List<String> = emptyList(),
    val selectedSkills: Set<String> = emptySet(),
    val currentSkills: List<StudentSkillResponse> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val removingSkill: String? = null,
    val errorRemoveMessage: String? = null
)

class SkillSelectionViewModel(
    private val skillRepository: SkillRepository,
    initialCurrentSkills: List<StudentSkillResponse>
) : ViewModel() {

    private val _uiState = MutableStateFlow(SkillSelectionUiState(currentSkills = initialCurrentSkills))
    val uiState: StateFlow<SkillSelectionUiState> = _uiState.asStateFlow()

    init {
        loadSkills()
    }

    fun loadSkills() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = skillRepository.getAllSkills()) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(isLoading = false, skills = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
            }
        }
    }

    fun toggleSkill(skillName: String) {
        val currentNames = _uiState.value.currentSkills.map { it.skillName }.toSet()
        if (skillName in currentNames) return
        _uiState.update { state ->
            val updated = if (skillName in state.selectedSkills)
                state.selectedSkills - skillName
            else
                state.selectedSkills + skillName
            state.copy(selectedSkills = updated, errorMessage = null)
        }
    }

    fun addCustomSkill(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val currentNames = _uiState.value.currentSkills.map { it.skillName.lowercase() }
        val alreadySelected = _uiState.value.selectedSkills.map { it.lowercase() }
        if (trimmed.lowercase() in currentNames || trimmed.lowercase() in alreadySelected) return
        _uiState.update { it.copy(selectedSkills = it.selectedSkills + trimmed, errorMessage = null) }
    }

    fun saveSkills(studentId: String) {
        val toSave = _uiState.value.selectedSkills.toList()
        if (toSave.isEmpty()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            for (skill in toSave) {
                when (val result = skillRepository.addSkillToStudent(studentId, skill)) {
                    is ApiResult.Success -> { /* proceed to next */ }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(isSaving = false, errorMessage = result.message) }
                        return@launch
                    }
                }
            }
            _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
        }
    }

    fun removeSkill(studentId: String, skillId: String, skillName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(removingSkill = skillName) }
            when (val result = skillRepository.removeSkillFromStudent(studentId, skillId)) {
                is ApiResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            removingSkill = null,
                            currentSkills = state.currentSkills.filter { it.skillName != skillName }
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(removingSkill = null, errorRemoveMessage = result.message) }
                }
            }
        }
    }
}

class SkillSelectionViewModelFactory(
    private val skillRepository: SkillRepository,
    private val currentSkills: List<StudentSkillResponse>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SkillSelectionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SkillSelectionViewModel(skillRepository, currentSkills) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
