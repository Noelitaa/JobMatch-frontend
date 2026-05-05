package com.moviles.jobmatch.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.jobmatch.data.CompanyMap
import com.moviles.jobmatch.data.CompanySummary
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
    val filteredCompanies: List<CompanySummary> = emptyList()
)

class SearchCompanyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var allCompanies: List<CompanySummary> = emptyList()

    init {
        loadCompaniesFromMap()

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

    private fun loadCompaniesFromMap() {
        allCompanies = CompanyMap.companies.map { (name, id) ->
            CompanySummary(
                id = id,
                companyName = name,
                email = ""
            )
        }
        _uiState.value = _uiState.value.copy(filteredCompanies = allCompanies)
    }

    fun updateSearchText(text: String) {
        _uiState.value = _uiState.value.copy(
            searchText = text,
            errorMessage = null
        )
    }

    private fun filterCompanies(query: String) {
        val filtered = if (query.isEmpty()) {
            allCompanies
        } else {
            allCompanies.filter { company ->
                company.companyName.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(filteredCompanies = filtered)
    }

    fun searchExactCompany(onCompanyFound: (String) -> Unit) {
        val query = _uiState.value.searchText
        if (query.isBlank()) return

        val found = allCompanies.find { company ->
            company.companyName.equals(query, ignoreCase = true)
        }

        if (found == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Empresa '$query' no encontrada"
            )
        } else {
            onCompanyFound(found.id)
        }
    }

    fun selectCompany(companyId: String, onCompanySelected: (String) -> Unit) {
        onCompanySelected(companyId)
    }
}