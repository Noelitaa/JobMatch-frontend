package com.moviles.jobmatch.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.CompanyMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchText: String = "",
    val errorMessage: String? = null,
    val filteredCompanies: List<Pair<String, String>> = emptyList(),
    val allCompanies: List<Pair<String, String>> = emptyList()
)

class SearchCompanyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        val all = CompanyMap.getAllCompanies()
        _uiState.value = _uiState.value.copy(
            allCompanies = all,
            filteredCompanies = all
        )

        viewModelScope.launch {
            _uiState
                .map { it.searchText }
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    filterCompanies(query)
                }
        }
    }

    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(
            searchText = text,
            errorMessage = null
        )
    }

    private fun filterCompanies(query: String) {
        val all = _uiState.value.allCompanies
        val filtered = if (query.isEmpty()) {
            all
        } else {
            all.filter { (name, _) ->
                name.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(filteredCompanies = filtered)
    }

    fun searchExactCompany(): String? {
        val query = _uiState.value.searchText
        if (query.isBlank()) return null

        val id = CompanyMap.getCompanyIdByName(query)
        if (id == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Empresa '$query' no encontrada"
            )
        }
        return id
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}